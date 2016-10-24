#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>

#define MESSAGE_BUF_SIZE 100
#define MAX_CLIENT_NUMBER 256
#define ROOM_NAME_MAX_SIZE 20

void * handle_clnt(void * arg);
void send_msg(char * msg, int len, int roomNum);
void error_handling(char * msg);

typedef struct _room {
	char room_name[ROOM_NAME_MAX_SIZE];
	int client_count;
	int client_fd[MAX_CLIENT_NUMBER];
	struct _room *next;
} room;

room *room_list;
int room_count;
pthread_mutex_t mutx;

int main(int argc, char *argv[])
{
	int serv_sock, clnt_sock;
	struct sockaddr_in serv_adr, clnt_adr;
	int clnt_adr_sz;
	pthread_t t_id;
	if(argc!=2) {
		printf("Usage : %s <port>\n", argv[0]);
		exit(1);
	}
	
	room_count = 0;
	room_list = NULL;
  
	pthread_mutex_init(&mutx, NULL);
	serv_sock=socket(PF_INET, SOCK_STREAM, 0);

	memset(&serv_adr, 0, sizeof(serv_adr));
	serv_adr.sin_family=AF_INET; 
	serv_adr.sin_addr.s_addr=htonl(INADDR_ANY);
	serv_adr.sin_port=htons(atoi(argv[1]));
	
	if(bind(serv_sock, (struct sockaddr*) &serv_adr, sizeof(serv_adr))==-1)
		error_handling("bind() error");
	if(listen(serv_sock, 5)==-1)
		error_handling("listen() error");
	
	while(1)
	{
		clnt_adr_sz=sizeof(clnt_adr);
		clnt_sock=accept(serv_sock, (struct sockaddr*)&clnt_adr,&clnt_adr_sz);
		
		pthread_create(&t_id, NULL, handle_clnt, (void*)&clnt_sock);
		pthread_detach(t_id);
		printf("Connected client IP: %s \n", inet_ntoa(clnt_adr.sin_addr));
	}
	close(serv_sock);
	return 0;
}

room* create_room(int client_sock, char room_name[]) {
	room *myroom = (room*)malloc(sizeof(room));
	if (myroom) {
		printf("creating room succeeded!\n");
		strcpy(myroom->room_name, room_name);
		myroom->client_count = 0;
		myroom->client_fd[myroom->client_count] = client_sock;
		myroom->client_count++;
		myroom->next = NULL;
		return myroom;
	}
	else {
		return NULL;
	}
}
	
void * handle_clnt(void * arg)
{
	int clnt_sock=*((int*)arg);
	room *myroom, *temp;
	int str_len=0, i, j, roomNum=0;
	int flag = 0;
	char msg[MESSAGE_BUF_SIZE];
	char seps[] = "\n";
	char *token = NULL;

	// get first message for setting
	if((str_len = read(clnt_sock, msg, sizeof(msg))) != 0) {
		token = strtok(msg, seps); // parsing the room name
		printf("room name : %s\n", token);
		pthread_mutex_lock(&mutx);
		// if a room exists
		if (room_list) {
			temp = room_list;
			while (temp) {
				// find same room
				if (!strcmp(token, temp->room_name)) {
					printf("found! - %s\n", temp->room_name);
					// add this client information to the room
					temp->client_fd[temp->client_count] = clnt_sock;
					temp->client_count++;
					myroom = temp;
					flag = 1;
					break;
				}
				temp = temp->next;
			}
			if (!flag) {
				printf("finding failed.. creating new room!\n");
				myroom = create_room(clnt_sock, token); // create a new room
				if (myroom) { // if dynamic allocation succeeded
					temp = room_list;
					while (temp->next) {
						temp = temp->next;
					}
					temp->next = myroom; // set last node
				}
				else { // if not, quit this thread
					close(clnt_sock);
					pthread_mutex_unlock(&mutx);
					return NULL;
				}
			}
		}
		else { // if no room exists
			printf("no room list\n");
			myroom = create_room(clnt_sock, token); // create a new room
			if (myroom) { // if dynamic allocation succeeded
				room_list = myroom; // set first room
			}
			else { // if not, quit this thread
				close(clnt_sock);
				pthread_mutex_unlock(&mutx);
				return NULL;
			}
		}

		/*for(i=0;i<room_count;i++) {
			printf("token : %s, room_name[%d] : %s\n", token, i, room_name[i]);
			if(!strcmp(token, room_name[i])) {
				printf("found it! i : %d, sock : %d\n", i, clnt_sock);
				client_fd[i][client_count[i]] = clnt_sock;
				client_count[i]++;
				roomNum=i;
				flag = 1;
				break;
			}
		}
		if(!flag) {
			printf("token : %s, sock : %d\n", token, clnt_sock);
			strcpy(room_name[room_count], token);
			client_fd[room_count][client_count[room_count]] = clnt_sock;
			client_count[room_count]++;
			roomNum= room_count;
			room_count++;
		}*/
		pthread_mutex_unlock(&mutx);
	}
	else { // if failed, quit this thread
		close(clnt_sock);
		return NULL;
	}
	
	// send a message to all clients in the room
	while ((str_len = read(clnt_sock, msg, sizeof(msg))) != 0) {
		pthread_mutex_lock(&mutx);
		for (i = 0; i < myroom->client_count; i++) {
			write(myroom->client_fd[i], msg, str_len);
		}
		pthread_mutex_unlock(&mutx);
	}
	
	// if this client quits
	pthread_mutex_lock(&mutx);
	for (i = 0; i < myroom->client_count; i++) {
		if (clnt_sock == myroom->client_fd[i]) {
			while (i < myroom->client_count - 1) {
				myroom->client_fd[i] = myroom->client_fd[i + 1];
				i++;
			}
			myroom->client_count--;
			if (myroom->client_count == 0) {
				temp = room_list;
				if (temp == myroom) {
					if (temp->next) {
						room_list = temp->next;
					}
					else {
						room_list = NULL;
					}
				}
				else {
					while (temp->next && temp->next != myroom) {
						temp = temp->next;
					}
					if (myroom->next) {
						temp->next = myroom->next;
					}
					else {
						temp->next = NULL;
					}
				}
				free(myroom);
				break;
			}
		}
	}
	/*for(i=0;i<client_count[roomNum];i++) {
		if(clnt_sock == client_fd[roomNum][i]) {
			while(i++<client_count[roomNum] - 1)
				client_fd[i][roomNum] = client_fd[i+1][roomNum];
			client_count[roomNum]--;
			break;
		}
	}
	if(client_count[roomNum] == 0) {
		i = roomNum;
		while(i++ < room_count -1) {
			strcpy(room_name[i], room_name[i+1]);
		}
		room_count--;
	}*/
	close(clnt_sock); 
	pthread_mutex_unlock(&mutx);
	return NULL;
}

void error_handling(char * msg)
{
	fputs(msg, stderr);
	fputc('\n', stderr);
	exit(1);
}
