=begin

 0. [YOU] signup
 1. [YOU] enter credentials
 2. cd android
 3. create a debug key
 4. download a secure token
 5. [YOU] sign it with a debug key
 6. [YOU] download API jar file and mvn clean deploy
 7. cd js/simple-example
 8. jar cf ../simple-example.zip .
 9. upload the zip file
10. cd rails
11. filter constants.rb with credentials
12. bundle install
13. filter constants.properties with credentials

[YOU] ... Do it yourself manually
Other items than annotated '[YOU]' are performed by `setup` task.
=end

cred = []
latest_token = nil

task :default => [:setup]

task :setup => [:get_credentials, :js, :android, :rails, :gae] do
  puts "Done.

  Unfortunately, I cannot help you actions regarding security token.
  Run the following commands respectively later (you can copy and paste them and run respectively).
  ---
cd simple-example-android
keytool -importkeystore -srckeystore $HOME/.android/debug.keystore -destkeystore $HOME/.android/debug.p12 -deststoretype PKCS12
  => enter 'android' when asked to enter password (asked 3 times)
openssl pkcs12 -in $HOME/.android/debug.p12 -clcerts -nokeys -out $HOME/.android/debug.pem
  => enter 'android' when asked to enter password
openssl pkcs12 -in $HOME/.android/debug.p12 -nocerts -out $HOME/.android/debug.key
  => enter 'android' when asked to enter password (asked 3 times)
openssl smime -sign -in #{latest_token} -out assets/moat/signed.bin -signer $HOME/.android/debug.pem -inkey $HOME/.android/debug.key -nodetach -outform der -binary
  ---
  Then download API jar and build APK.
  ---
iidn sysdownload inventit-dmc-android-lib-api-4.0.0-prod.jar
  => enter app_id, client_id and cleint_secret again
mvn install:install-file \
  -Dfile=inventit-dmc-android-lib-api-4.0.0-prod.jar \
  -DgroupId=com.yourinventit \
  -DartifactId=inventit-dmc-android-lib-api \
  -Dversion=4.0.0 -Dclassifier=prod -Dpackaging=jar
mvn clean deploy
  "
end

task :get_credentials do
  %w{app_id client_id client_secret}.each do |item|
    puts "Enter #{item}:"
    input = STDIN.gets.chomp
    raise "cannot be empty" if input.empty?
    cred << "--#{item}=#{input}"
  end
end

task :android do
  cwd = "simple-example-android"
  # create a debug key
  android_dir = File.join(Dir.home, ".android")
  Dir.mkdir(android_dir) unless File.exists?(android_dir)
  exec_wd cwd, "keytool -genkey -keypass android -keystore $HOME/.android/debug.keystore -alias androiddebugkey -storepass android -validity 10000 -dname 'CN=Android Debug,O=Android,C=US'" unless File.exists?(File.join(android_dir, "debug.keystore"))
  # download a secure token and sign it with a debug key
  iidn cwd, "tokengen", "simple-example", "#{cred.join(' ')}"
  latest_token = Dir.entries(cwd).sort_by{|f| File.mtime(File.join(cwd,f))}.select{|f| f.end_with?('.bin')}.reverse[0]
end

task :js do
  cwd = "simple-example-js"
  # jar cf ../simple-example.zip .
  exec_wd cwd, "jar cf simple-example.zip simple-example"
  # upload the zip file
  iidn cwd, "jsdeploy", "simple-example.zip", "#{cred.join(' ')}"
end

task :rails do
  cwd = "simple-example-rails"
  # filter constants.rb with credentials
  filename = "#{cwd}/config/initializers/constants.rb"
  constants = File.read(filename) 
  constants.gsub!("{:your_application_id}", cred[0][(cred[0].index('=') + 1)..-1])
  constants.gsub!("{:your_client_id}", cred[1][(cred[1].index('=') + 1)..-1])
  constants.gsub!("{:your_client_secret}", cred[2][(cred[2].index('=') + 1)..-1])
  File.open(filename, "w") { |file| file << constants }
  # bundle install
  exec_wd cwd, "bundle install"
  # rake db:migrate
  exec_wd cwd, "rake db:migrate"
end

task :gae do
  cwd = "simple-example-gae"
  # filter constants.properties with credentials
  filename = "#{cwd}/src/constants.properties"
  constants = File.read(filename) 
  constants.gsub!("{:your_application_id}", cred[0][(cred[0].index('=') + 1)..-1])
  constants.gsub!("{:your_client_id}", cred[1][(cred[1].index('=') + 1)..-1])
  constants.gsub!("{:your_client_secret}", cred[2][(cred[2].index('=') + 1)..-1])
  File.open(filename, "w") { |file| file << constants }
end

def exec_wd(cwd, command)
  system "cd #{cwd} && #{command}"
end

def iidn(*args)
  system "cd #{args[0]} && iidn #{args[1..-1].join(' ')}"
end