# Java erasure

A Java erasure codes wrapper library for Jerasure(https://bitbucket.org/jimplank/jerasure)

## Usage
### Install gf-complete
    git clone git@bitbucket.org:jimplank/gf-complete.git
    cd gf-complete
    ./autogen.sh
    ./configure
    make
    sudo make install
### Install jerasure
    git clone git@bitbucket.org:jimplank/jerasure.git
    cd jerasure
    autoreconf --force --install -I m4
    ./configure
    make
    sudo make install
### Compile and Run Your Application
#### 1. Add following dependency to your application's pom.xml:
    <groupId>com.xiaomi.infra</groupId>    
    <artifactId>java-erasure</artifactId>  
    <version>1.0-SNAPSHOT</version>
#### 2. Compile your application
    mvn clean package
#### 3. Run your application
    Add the following system property to your application:
      Mac OS:  -Djna.library.path=${path_to_libJerasure.dylib}
      Linux:   -Djna.library.path=${path_to_libJerasure.so}
      Windows: -Djna.library.path=${path_to_libJerasure.dll}
    Then run your application.
    