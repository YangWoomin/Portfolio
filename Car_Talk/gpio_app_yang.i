%module gpioYang
%{
extern void writeMessage(int rotateNum, int speed, char message[1024]);
%}

extern void writeMessage(int rotateNum, int speed, char message[1024]);
