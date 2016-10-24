#pragma once
#include <thread>
#include <stdlib.h>
#include <gl/glut.h>
#include <math.h>
#include <time.h>
#include <Windows.h>
#include <winsock2.h>
#include <sys/types.h>
#include <stdio.h>
#include <io.h>

#define X 6
#define Y 16
#define Z 6

extern int myBlock[X][Y][Z];
extern int rivalBlock[X][Y][Z];
extern int currentBlock[9][3];
extern int fd;

void allClear();
void createBlock();
void recreateBlock();
int moveBlock(int direction);
void changeBlock(int rotation);
void downCurrentBlock();
void sendBlock();