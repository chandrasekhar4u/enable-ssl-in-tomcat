keytool -genkeypair -alias client2key -keyalg RSA -dname "CN=client2,OU=Application Development,O=Highwinds,L=Winter Park,S=FL,C=US" -keypass password -storepass password -keystore client2.jks

keytool -exportcert -alias client2key -file client2-public.cer -keystore client2.jks -storepass password
keytool -importcert -keystore server.jks -alias client2cert -file client2-public.cer -storepass password -noprompt
 

keytool -exportcert -alias serverkey -file server-public.cer -keystore server.jks -storepass password
keytool -importcert -keystore client2.jks -alias servercert -file server-public.cer -storepass password -noprompt
 
# view the contents of the keystore (use -v for verbose output)
keytool -list -keystore server.jks -storepass password

# view the contents of the keystore (use -v for verbose output)
keytool -list -keystore client2.jks -storepass password