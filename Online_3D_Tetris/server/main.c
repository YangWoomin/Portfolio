#include <stdio.h>
#include <winsock2.h>

#define X 6
#define Y 16
#define Z 6

typedef struct client {
	int fd;
	char id[50];
	struct client *rival;
	struct client *next;
}client;

int main() {
	int serverSock, clientSock;
	int serverLength, clientLength;
	WSADATA wsa;
	struct sockaddr_in serverAddress, clientAddress;
	int result;
	fd_set readfds, testfds;

	result = WSAStartup(MAKEWORD(2, 2), &wsa);
	if (result != 0) {
		perror("WSA failed");
		exit(EXIT_FAILURE);
	}
	
	client *clients = NULL;
	client *nextTemp = NULL;
	client *clientTemp = NULL;

	struct tm *tmPrt;
	time_t theTime;
	char currentTime[20];
	
	serverSock = socket(AF_INET, SOCK_STREAM, 0);
	serverAddress.sin_family = AF_INET;
	serverAddress.sin_addr.s_addr = htonl(INADDR_ANY);
	serverAddress.sin_port = htons(9734);
	serverLength = sizeof(serverAddress);

	result = bind(serverSock, (struct sockaddr *)&serverAddress, serverLength);
	if (result == SOCKET_ERROR) {
		perror("Biding failed");
		exit(EXIT_FAILURE);
	}

	result = listen(serverSock, 5);
	if (result != 0) {
		perror("Listen faield");
		exit(EXIT_FAILURE);
	}

	FD_ZERO(&readfds);
	FD_SET(serverSock, &readfds);

	while (1) {
		int fd;
		char buffer[X*Y*Z];
		int nread;
		int check;

		testfds = readfds;

		printf("Server waiting\n");
		result = select(FD_SETSIZE, &testfds, (fd_set *)0, (fd_set*)0, (struct timeval *)0);
		if (result < 1) {
			perror("Select failed");
			exit(EXIT_FAILURE);
		}
		if (FD_ISSET(serverSock, &testfds)) {
			clientLength = sizeof(clientAddress);
			clientSock = accept(serverSock, (struct sockaddr *)&clientAddress, &clientLength);
			FD_SET(clientSock, &readfds);
			if (!clients) {
				clients = (client *)malloc(sizeof(client));
				clients->fd = clientSock;
				clients->rival = NULL;
				clients->next = NULL;
				memset(clients->id, '\0', sizeof(clients->id));
			}
			else {
				nextTemp = clients;
				while (nextTemp->next) {
					nextTemp = nextTemp->next;
				}
				clientTemp = (client *)malloc(sizeof(client));
				clientTemp->fd = clientSock;
				clientTemp->rival = NULL;
				clientTemp->next = NULL;
				memset(clientTemp->id, '\0', sizeof(clientTemp->id));
				nextTemp->next = clientTemp;
			}
			(void)time(&theTime);
			memset(currentTime, '\0', sizeof(currentTime));
			strcpy(currentTime, ctime(&theTime), 19);
			printf("New client is trying to connect - %s", currentTime);
		}
		else {
			nextTemp = clients;
			while (!FD_ISSET(nextTemp->fd, &testfds) && nextTemp->next) {
				nextTemp = nextTemp->next;
			}
			fd = nextTemp->fd;
			ioctlsocket(fd, FIONREAD, &nread);
			if (nread == 0) {
				nextTemp = clients;
				if (nextTemp->fd == fd) {
					clientTemp = nextTemp;
					if (clients->next) {
						clients = clients->next;
					}
					else {
						clients = NULL;
					}
				}
				else if (nextTemp->next) {
					while (nextTemp->next && fd != nextTemp->next->fd) {
						nextTemp = nextTemp->next;
					}
					clientTemp = nextTemp->next;
					if (clientTemp->next) {
						nextTemp->next = clientTemp->next;
					}
					else {
						nextTemp->next = NULL;
					}
				}
				if (clientTemp->rival) {
					//write(clientTemp->rival->fd, "connectionfailure", sizeof("connectionfailure"));
					send(clientTemp->rival->fd, "connectionfailure", sizeof("connectionfailure"), 0);
					clientTemp->rival->rival = NULL;
				}
				printf("Removed client %s - %s", clientTemp->id, currentTime);
				FD_CLR(clientTemp->fd, &readfds);
				//close(fd);
				closesocket(clientTemp->fd);
				free(clientTemp);
			}
			else {
				nextTemp = clients;
				while (nextTemp->fd != fd) {
					if (nextTemp->next) {
						nextTemp = nextTemp->next;
					}
					else {
						break;
					}
				}
				clientTemp = nextTemp;
				if ((int)strlen(clientTemp->id) >= 1) {
					int temp[X][Y][Z] = { 0 }; //
					recv(fd, (char*)temp, sizeof(temp), 0);
					printf("Received information from id = %s\n", clientTemp->id);
					//write(clientTemp->rival->fd, buffer, sizeof(buffer));
					if (clientTemp->rival) {
						send(clientTemp->rival->fd, (char*)temp, sizeof(temp), 0);
					}
				}
				else {
					memset(buffer, '\0', sizeof(buffer));
					//read(fd, buffer, sizeof(buffer));
					recv(fd, buffer, sizeof(buffer), 0);
					nextTemp = clients;
					int check = 0;
					while (nextTemp) {
						if ((int)strlen(nextTemp->id) >= 1) {
							if (!strcmp(buffer, nextTemp->id)) {
								break;
							}
						}
						if (!(nextTemp->next)) {
							check = 1;
						}
						nextTemp = nextTemp->next;
					}
					if (check) {
						//write(fd, "yang-3521", sizeof("yang-3521"));
						send(fd, "yang-3521", sizeof("yang-3521"), 0);
						strcpy(clientTemp->id, buffer);
						printf("Created new id : %s\n", clientTemp->id);
						Sleep(3000);
						nextTemp = clients;
						while (nextTemp) {
							if (!nextTemp->rival && nextTemp->fd != clientTemp->fd && (int)strlen(nextTemp->id) >= 1) {
								clientTemp->rival = nextTemp;
								nextTemp->rival = clientTemp;
								//write(nextTemp->fd, "start", sizeof("start"));
								send(nextTemp->fd, "start", sizeof("start"), 0);
								//write(clientTemp->fd, "start", sizeof("start"));
								send(clientTemp->fd, "start", sizeof("start"), 0);
							}
							nextTemp = nextTemp->next;
						}
						if (!clientTemp->rival) {
							//write(fd, "wait", sizeof("wait"));
							send(fd, "wait", sizeof("wait"), 0);
							printf("The client %s is waiting for new game\n", clientTemp->id);
						}
					}
					else {
						//write(fd, "identificationfailure", sizeof("identificationfailure"));
						send(fd, "identificationfailure", sizeof("identificationfailure"), 0);
						printf("Failed to creating new id\n");
					}
				}
			}
		}
	}
	closesocket(serverSock);
	closesocket(clientSock);
	WSACleanup();
	exit(EXIT_SUCCESS);
}