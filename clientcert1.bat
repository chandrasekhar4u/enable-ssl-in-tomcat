keytool -genkeypair -alias serverkey -keyalg RSA -dname "CN=localhost,OU=Application Development,O=GoSmarter,L=Bangalore,ST=KA,C=IN" -keypass password -keystore server.jks -storepass password

keytool -genkeypair -alias client1key -keyalg RSA -dname "CN=client1,OU=Application Development,O=GoSmarter,L=Bangalore,ST=KA,C=IN" -keypass password -storepass password -keystore client1.jks

keytool -exportcert -alias client1key -file client1-public.cer -keystore client1.jks -storepass password
keytool -importcert -keystore server.jks -alias client1cert -file client1-public.cer -storepass password -noprompt
 
# view the contents of the keystore (use -v for verbose output)
keytool -list -keystore server.jks -storepass password


keytool -exportcert -alias serverkey -file server-public.cer -keystore server.jks -storepass password
keytool -importcert -keystore client1.jks -alias servercert -file server-public.cer -storepass password -noprompt
 
# view the contents of the keystore (use -v for verbose output)
keytool -list -keystore client1.jks -storepass password