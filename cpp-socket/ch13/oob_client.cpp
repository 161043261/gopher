//
// Created by Tiancheng on 2024/8/25.
//

#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>

#include <cstdio>
#include <cstdlib>
#include <cstring>

#define BUF_SIZE 30

int main(int argc, char *argv[]) {
  if (argc != 3) {
    printf("Usage: %s <serverAddr> <serverPort>\n", argv[0]);
    exit(1);
  }

  //* 调用 socket 函数，创建 socket 套接字
  int clientSocketFd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
  if (clientSocketFd == -1) {
    printf("Error created socket\n");
  }
  sockaddr_in serverAddr{};
  serverAddr.sin_family = AF_INET;
  serverAddr.sin_addr.s_addr = inet_addr(argv[1]);
  serverAddr.sin_port = htons(atoi(argv[2]));

  //* 客户端调用 connect 函数，向服务器发送连接请求
  if (connect(clientSocketFd, (sockaddr *)&serverAddr, sizeof(serverAddr)) ==
      -1) {
    printf("Error connected to server\n");
    exit(1);
  }

  write(clientSocketFd, "123", strlen("123"));
  send(clientSocketFd, "4", strlen("4"), MSG_OOB);  //! 紧急字节 4
  close(clientSocketFd);
  return 0;
}
