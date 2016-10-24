#include "header.h"

void allClear() {
	for (int i = 0; i < X; i++) {
		for (int j = 0; j < Y; j++) {
			for (int k = 0; k < Z; k++) {
				myBlock[i][j][k] = 0;
			}
		}
	}
}

int moveBlock(int direction) {
	switch (direction) {
	case 0 : // left
		for (int i = 0; i < 8; i++)
			if(currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				if (currentBlock[i][0] == 0 || myBlock[currentBlock[i][0] - 1][currentBlock[i][1]][currentBlock[i][2]] > 0)
					return -1;
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				currentBlock[i][0]--;
		break;
	case 1 : // right
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				if (currentBlock[i][0] == X - 1 || myBlock[currentBlock[i][0] + 1][currentBlock[i][1]][currentBlock[i][2]] > 0)
					return -1;
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				currentBlock[i][0]++;
		break;
	case 2 : // bottom
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				if (currentBlock[i][1] == 0 || myBlock[currentBlock[i][0]][currentBlock[i][1] - 1][currentBlock[i][2]] > 0) {
					recreateBlock();
					return -1;
				}
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				currentBlock[i][1]--;
		break;
	case 3 : // far
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				if (currentBlock[i][2] == 0 || myBlock[currentBlock[i][0]][currentBlock[i][1]][currentBlock[i][2] - 1] > 0)
					return -1;
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				currentBlock[i][2]--;
		break;
	case 4 : // near
		for (int i = 0; i < 8; i++)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				if (currentBlock[i][2] == Z - 1 || myBlock[currentBlock[i][0]][currentBlock[i][1]][currentBlock[i][2] + 1] > 0)
					return -1;
		for (int i = 7; i >= 0; i--)
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1)
				currentBlock[i][2]++;
		break;
	default :
		break;
	}

	// send
	sendBlock();
	return 0;
}

void createBlock() {
	int randNum;
	srand(GetTickCount());
	randNum = rand() % 5;
	for (int i = 0; i < 9; i++)
		for (int j = 0; j < 3; j++)
			currentBlock[i][j] = -1;
	switch (randNum) {
	case 0 :
		currentBlock[0][0] = currentBlock[1][0] = currentBlock[2][0] = currentBlock[3][0] = (int)(X / 2) - 1;
		currentBlock[4][0] = currentBlock[5][0] = currentBlock[6][0] = currentBlock[7][0] = (int)(X / 2);
		currentBlock[0][1] = currentBlock[1][1] = currentBlock[4][1] = currentBlock[5][1] = Y - 2;
		currentBlock[2][1] = currentBlock[3][1] = currentBlock[6][1] = currentBlock[7][1] = Y - 1;
		currentBlock[0][2] = currentBlock[2][2] = currentBlock[4][2] = currentBlock[6][2] = (int)(Z / 2) - 1;
		currentBlock[1][2] = currentBlock[3][2] = currentBlock[5][2] = currentBlock[7][2] = (int)(Z / 2);
		currentBlock[8][0] = 1;
		if (myBlock[currentBlock[0][0]][currentBlock[0][1]][currentBlock[0][2]] > 0
			|| myBlock[currentBlock[1][0]][currentBlock[1][1]][currentBlock[1][2]] > 0
			|| myBlock[currentBlock[2][0]][currentBlock[2][1]][currentBlock[2][2]] > 0
			|| myBlock[currentBlock[3][0]][currentBlock[3][1]][currentBlock[3][2]] > 0
			|| myBlock[currentBlock[4][0]][currentBlock[4][1]][currentBlock[4][2]] > 0
			|| myBlock[currentBlock[5][0]][currentBlock[5][1]][currentBlock[5][2]] > 0
			|| myBlock[currentBlock[6][0]][currentBlock[6][1]][currentBlock[6][2]] > 0
			|| myBlock[currentBlock[7][0]][currentBlock[7][1]][currentBlock[7][2]] > 0)
			allClear();
		return;
	case 1 :
		currentBlock[0][0] = currentBlock[1][0] = currentBlock[2][0] = currentBlock[3][0] = (int)(X / 2) - 1;
		currentBlock[0][1] = currentBlock[1][1] = Y - 1;
		currentBlock[2][1] = Y - 2;
		currentBlock[3][1] = Y - 3;
		currentBlock[0][2] = (int)(Z / 2) - 1;
		currentBlock[1][2] = currentBlock[2][2] = currentBlock[3][2] = (int)(Z / 2);
		currentBlock[8][0] = 2;
		break;
	case 2 :
		currentBlock[0][0] = currentBlock[1][0] = currentBlock[2][0] = currentBlock[3][0] = (int)(X / 2) - 1;
		currentBlock[0][1] = currentBlock[1][1] = Y - 1;
		currentBlock[2][1] = currentBlock[3][1] = Y - 2;
		currentBlock[0][2] = (int)(Z / 2) - 1;
		currentBlock[1][2] = currentBlock[2][2] = (int)(Z / 2);
		currentBlock[3][2] = (int)(Z / 2) + 1;
		currentBlock[8][0] = 3;
		break;
	case 3 :
		currentBlock[0][0] = currentBlock[1][0] = currentBlock[2][0] = currentBlock[3][0] = (int)(X / 2) - 1;
		currentBlock[0][1] = Y - 1;
		currentBlock[1][1] = currentBlock[2][1] = currentBlock[3][1] = Y - 2;
		currentBlock[0][2] = currentBlock[1][2] = (int)(Z / 2);
		currentBlock[2][2] = (int)(Z / 2) - 1;
		currentBlock[3][2] = (int)(Z / 2) + 1;
		currentBlock[8][0] = 4;
		break;
	case 4 :
		currentBlock[0][0] = currentBlock[1][0] = currentBlock[2][0] = currentBlock[3][0] = (int)(X / 2) - 1;
		currentBlock[0][1] = currentBlock[1][1] = currentBlock[2][1] = currentBlock[3][1] = Y - 1;
		currentBlock[0][2] = (int)(Z / 2) - 1;
		currentBlock[1][2] = (int)(Z / 2);
		currentBlock[2][2] = (int)(Z / 2) + 1;
		currentBlock[3][2] = (int)(Z / 2) + 2;
		currentBlock[8][0] = 5;
		break;
	default:
		break;
	}
	if (myBlock[currentBlock[0][0]][currentBlock[0][1]][currentBlock[0][2]] > 0
		|| myBlock[currentBlock[1][0]][currentBlock[1][1]][currentBlock[1][2]] > 0
		|| myBlock[currentBlock[2][0]][currentBlock[2][1]][currentBlock[2][2]] > 0
		|| myBlock[currentBlock[3][0]][currentBlock[3][1]][currentBlock[3][2]] > 0)
		allClear();

	// send
	sendBlock();
}

void recreateBlock() {
	for (int i = 0; i < 8; i++) {
		if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
			myBlock[currentBlock[i][0]][currentBlock[i][1]][currentBlock[i][2]] = currentBlock[8][0];
		}
	}
	for (int i = 0; i < Y; i++) {
		int flag = 0;
		int count = 0;
		for (int j = 0; j < Z; j++) {
			for (int k = 0; k < X; k++) {
				if (myBlock[k][i][j] == 0) {
					flag = 1;
					count++;
				}
			}
		}
		if (flag == 0) {
			for (int j = 0; j < Z; j++) {
				for (int k = 0; k < X; k++) {
					myBlock[k][i][j] = 0;
				}
			}
			if (i < Y - 1) {
				for (int n = i + 1; n < Y; n++) {
					int count2 = 0;
					for (int j = 0; j < Z; j++) {
						for (int k = 0; k < X; k++) {
							if (myBlock[k][n][j] != 0) {
								myBlock[k][n - 1][j] = myBlock[k][n][j];
								myBlock[k][n][j] = 0;
							}
							else {
								count2++;
							}
						}
					}
					if (count2 == X*Z) {
						break;
					}
				}
			}
		}
		if (count == X*Z) {
			break;
		}
	}
	createBlock();
}

void changeBlock(int rotation) {
	int x[8];
	int y[8];
	int z[8];
	if (currentBlock[8][0] == 1)
		return;
	switch (rotation) {
	case 0 : // left
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				x[i] = currentBlock[i][0] - currentBlock[1][0];
				z[i] = currentBlock[1][2] - currentBlock[i][2];
				if (currentBlock[1][0] + z[i] < 0 || currentBlock[1][0] + z[i] > X - 1
					|| currentBlock[1][2] + x[i] < 0 || currentBlock[1][2] + x[i] > Z - 1
					|| myBlock[currentBlock[1][0] + z[i]][currentBlock[i][1]][currentBlock[1][2] + x[i]] != 0)
					return;
			}
		}
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				currentBlock[i][0] = currentBlock[1][0] + z[i];
				currentBlock[i][2] = currentBlock[1][2] + x[i];
			}
		}
		break;
	case 1 : // right
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				x[i] = currentBlock[i][0] - currentBlock[1][0];
				z[i] = currentBlock[1][2] - currentBlock[i][2];
				if (currentBlock[1][0] - z[i] < 0 || currentBlock[1][0] - z[i] > X - 1
					|| currentBlock[1][2] - x[i] < 0 || currentBlock[1][2] - x[i] > Z - 1
					|| myBlock[currentBlock[1][0] - z[i]][currentBlock[i][1]][currentBlock[1][2] - x[i]] != 0)
					return;
			}
		}
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				currentBlock[i][0] = currentBlock[1][0] - z[i];
				currentBlock[i][2] = currentBlock[1][2] - x[i];
			}
		}
		break;
	case 2 : // up
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				y[i] = currentBlock[i][1] - currentBlock[1][1];
				z[i] = currentBlock[1][2] - currentBlock[i][2];
				if (currentBlock[1][1] - z[i] < 0 || currentBlock[1][1] - z[i] > Y - 1
					|| currentBlock[1][2] - y[i] < 0 || currentBlock[1][2] - y[i] > Z - 1
					|| myBlock[currentBlock[i][0]][currentBlock[1][1] - z[i]][currentBlock[1][2] - y[i]] != 0)
					return;
			}
		}
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				currentBlock[i][1] = currentBlock[1][1] - z[i];
				currentBlock[i][2] = currentBlock[1][2] - y[i];
			}
		}
		break;
	case 3 : // down
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				y[i] = currentBlock[i][1] - currentBlock[1][1];
				z[i] = currentBlock[1][2] - currentBlock[i][2];
				if (currentBlock[1][1] + z[i] < 0 || currentBlock[1][1] + z[i] > Y - 1
					|| currentBlock[1][2] + y[i] < 0 || currentBlock[1][2] + y[i] > Z - 1
					|| myBlock[currentBlock[i][0]][currentBlock[1][1] + z[i]][currentBlock[1][2] + y[i]] != 0)
					return;
			}
		}
		for (int i = 0; i < 8; i++) {
			if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
				currentBlock[i][1] = currentBlock[1][1] + z[i];
				currentBlock[i][2] = currentBlock[1][2] + y[i];
			}
		}
		break;
	default:
		break;
	}

	// send
	sendBlock();
}

void downCurrentBlock() {
	while (moveBlock(2) != -1);
	glutPostRedisplay();
}