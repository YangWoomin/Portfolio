#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <linux/kdev_t.h>
#include <string.h>

void writeMessage(int rotateNum, int speed, char message[1024]) {
	int fd, i, j;
	char buffer[2];
	char dev[] = "/dev/gpio_yang";

	if((fd = open(dev, O_RDWR)) < 0) {
		perror("open failed");
		exit(1);
	}

	buffer[0] = (char)speed;
	for(i=0; i<rotateNum; i++) {
		for(j=0; j<strlen(message); j++) {
			buffer[1] = message[j];
			write(fd, buffer, strlen(buffer));
			if(rotateNum == 9) {
				i--;
			}
		}
	}
	close(fd);
}
