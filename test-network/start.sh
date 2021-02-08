rm -rf ../wallet/*
./network.sh down
./network.sh up createChannel -ca -s couchdb
./network.sh deployCC -ccl java -ccn fabcar -ccp '../chaincode/chaincode/java' -cci initLedger
