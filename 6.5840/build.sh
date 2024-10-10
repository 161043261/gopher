cd /usr/local

sudo curl https://go.dev/dl/go1.17.13.linux-amd64.tar.gz -O

sudo tar -xzf ./go1.17.13.linux-amd64.tar.gz

sudo rm -rf ./go1.17.13.linux-amd64.tar.gz

go install golang.org/x/tools/gopls@v0.11.0

cd path/to/6.5840

git clone git://g.csail.mit.edu/6.824-golabs-2020 6.824

############################################################

cd src/main

go build -buildmode=plugin ../mrapps/wc.go

ls | grep .so

rm mr-out*

go run mrsequential.go wc.so pg*.txt
