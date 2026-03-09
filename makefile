SHELL := /bin/bash

STORES := stores
PASS   := password
DAYS   := 365
HOST   := localhost

CLIENTS := Alice Bob Charlie David Eva

CA_SUBJ     := /CN=CA
SERVER_SUBJ := /CN=$(HOST)

.PHONY: all clean verify clients client-keystores client-truststores

all: $(STORES)/serverkeystore $(STORES)/servertruststore clients verify
	@echo
	@echo "✅ Done. Generated keystores/truststores in ./stores"
	@echo "   Server keystore:     stores/serverkeystore"
	@echo "   Server truststore:   stores/servertruststore"
	@echo "   Client keystores:    stores/{Alice,Bob,Charlie,David,Eva}keystore"
	@echo "   Client truststores:  stores/{Alice,Bob,Charlie,David,Eva}truststore"
	@echo

clients: client-keystores client-truststores

client-keystores: $(foreach c,$(CLIENTS),$(STORES)/$(c)keystore)
client-truststores: $(foreach c,$(CLIENTS),$(STORES)/$(c)truststore)

# ---- CA ----
$(STORES)/ca.key $(STORES)/ca.pem: | $(STORES)
	@echo "==> Generating CA key + self-signed CA cert ($(DAYS) days)"
	openssl genrsa -out $(STORES)/ca.key 2048
	openssl req -x509 -new -nodes -key $(STORES)/ca.key \
	  -sha256 -days $(DAYS) -subj "$(CA_SUBJ)" -out $(STORES)/ca.pem

# ---- Server ----
$(STORES)/server.key:
	@echo "==> Generating server key"
	openssl genrsa -out $@ 2048

$(STORES)/server.csr: $(STORES)/server.key
	@echo "==> Generating server CSR"
	openssl req -new -key $< -subj "$(SERVER_SUBJ)" -out $@

$(STORES)/server-cert.pem: $(STORES)/server.csr $(STORES)/ca.key $(STORES)/ca.pem
	@echo "==> Signing server cert with CA (SAN + serverAuth)"
	openssl x509 -req -in $< \
	  -CA $(STORES)/ca.pem -CAkey $(STORES)/ca.key -CAcreateserial \
	  -out $@ -days $(DAYS) -sha256 \
	  -extfile <(printf "subjectAltName=DNS:$(HOST)\nextendedKeyUsage=serverAuth\nkeyUsage=digitalSignature,keyEncipherment\n")

$(STORES)/server.p12: $(STORES)/server.key $(STORES)/server-cert.pem $(STORES)/ca.pem
	@echo "==> Creating server PKCS12 keystore"
	openssl pkcs12 -export \
	  -inkey $(STORES)/server.key -in $(STORES)/server-cert.pem -certfile $(STORES)/ca.pem \
	  -name "serverkey" -out $@ -passout pass:$(PASS)

$(STORES)/serverkeystore: $(STORES)/server.p12
	@cp -f $< $@

$(STORES)/servertruststore: $(STORES)/ca.pem | $(STORES)
	@echo "==> Creating server truststore (trust CA)"
	@rm -f $@
	keytool -importcert -noprompt \
	  -alias ca -file $(STORES)/ca.pem \
	  -keystore $@ -storetype PKCS12 \
	  -storepass $(PASS)

# ---- Clients (named pattern rules) ----
$(STORES)/%.key:
	@echo "==> Generating $* key"
	openssl genrsa -out $@ 2048

$(STORES)/%.csr: $(STORES)/%.key
	@echo "==> Generating $* CSR"
	openssl req -new -key $< -subj "/CN=$*" -out $@

$(STORES)/%-cert.pem: $(STORES)/%.csr $(STORES)/ca.key $(STORES)/ca.pem
	@echo "==> Signing $* cert with CA (clientAuth)"
	openssl x509 -req -in $< \
	  -CA $(STORES)/ca.pem -CAkey $(STORES)/ca.key -CAcreateserial \
	  -out $@ -days $(DAYS) -sha256 \
	  -extfile <(printf "extendedKeyUsage=clientAuth\nkeyUsage=digitalSignature\n")

$(STORES)/%.p12: $(STORES)/%.key $(STORES)/%-cert.pem $(STORES)/ca.pem
	@echo "==> Creating $* PKCS12 keystore"
	openssl pkcs12 -export \
	  -inkey $(STORES)/$*.key -in $(STORES)/$*-cert.pem -certfile $(STORES)/ca.pem \
	  -name "$*key" -out $@ -passout pass:$(PASS)

$(STORES)/%keystore: $(STORES)/%.p12
	@cp -f $< $@

$(STORES)/%truststore: $(STORES)/ca.pem | $(STORES)
	@echo "==> Creating $* truststore (trust CA)"
	@rm -f $@
	keytool -importcert -noprompt \
	  -alias ca -file $(STORES)/ca.pem \
	  -keystore $@ -storetype PKCS12 \
	  -storepass $(PASS)

# ---- Utilities ----
$(STORES):
	@mkdir -p $(STORES)

verify:
	@echo
	@echo "==> Verify (keytool list)"
	@echo "--- serverkeystore ---"
	@keytool -list -v -keystore $(STORES)/serverkeystore -storetype PKCS12 -storepass $(PASS) | egrep "Keystore type:|Alias name:|Entry type:|Owner:|Issuer:|Valid from:"
	@echo

	@for c in $(CLIENTS); do \
	  echo "--- $${c}keystore ---"; \
	  keytool -list -v -keystore $(STORES)/$${c}keystore -storetype PKCS12 -storepass $(PASS) | egrep "Keystore type:|Alias name:|Entry type:|Owner:|Issuer:|Valid from:"; \
	  echo; \
	done

clean:
	rm -rf $(STORES)