#include "header.h"

GLfloat width, height;
GLfloat widthFactor, heightFactor;
GLfloat firstX, firstY, mouseX, mouseY;
GLfloat myAngleX = 0, myAngleY = 0;
GLfloat rivalAngleX = 0, rivalAngleY = 0;

int myBlock[X][Y][Z];
int rivalBlock[X][Y][Z];
int currentBlock[9][3];
int fd;

void MyLightInit() {
	GLfloat global_ambient[] = { 0.1, 0.1, 0.1, 0.1 };
	GLfloat light0_ambient[] = { 0.5, 0.4, 0.3, 1.0 };
	GLfloat light0_diffuse[] = { 0.5, 0.4, 0.3, 0.1 };
	GLfloat light0_specular[] = { 1.0, 1.0, 1.0, 1.0 };

	GLfloat material_ambient[] = { 0.3, 0.3, 0.3, 1.0 };
	GLfloat material_diffuse[] = { 0.8, 0.8, 0.8, 1.0 };
	GLfloat material_specular[] = { 0.0, 0.0, 1.0, 1.0 };
	GLfloat material_shininess[] = { 25.0 };

	GLfloat LightPosition0[] = { X/2, Y/2+1, Z/2, 1.0 };

	glShadeModel(GL_SMOOTH);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_LIGHTING);
	glEnable(GL_COLOR_MATERIAL);

	glEnable(GL_LIGHT0);
	glLightfv(GL_LIGHT0, GL_AMBIENT, light0_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light0_diffuse);
	glLightfv(GL_LIGHT0, GL_SPECULAR, light0_specular);

	
	glMaterialfv(GL_FRONT, GL_DIFFUSE, material_diffuse);
	glMaterialfv(GL_FRONT, GL_SPECULAR, material_specular);
	glMaterialfv(GL_FRONT, GL_AMBIENT, material_ambient);
	glMaterialfv(GL_FRONT, GL_SHININESS, material_shininess);
	

	glLightfv(GL_LIGHT0, GL_POSITION, LightPosition0);
	glLightModelfv(GL_LIGHT_MODEL_AMBIENT, global_ambient);
}

// the function for keyboard event
void MyKeyboard(unsigned char pressed, int x, int y) {
	switch (pressed) {
	case 32 :
		downCurrentBlock();
		break;
	case 'q': // if character q is pressed, quit this program
		exit(0);
		break;
	case 'Q': // or if Q is preseed
		exit(0);
		break;
	case 27: // or if ESC key is pressed
		exit(0);
		break;
	default:
		break;
	}
}

void MyDisplay() {
	int num = 0;
	glClearDepth(1.0f);
	glClearColor(0, 0, 0, 1);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(-(X + 2)*widthFactor, (X + 2)*widthFactor, -(Y/2 + 5)*heightFactor, (Y/2 + 5)*heightFactor, 3, Y*2);
	MyLightInit();
	
	/* rival user */
	glViewport(0, 0, width / 2, height);

	// change matrix mode to model view mode
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	gluLookAt(0, 0, Y + 3, 0, 0, -1, 0, 1, 0);

	// for rotating x-axis or y-axis
	glRotatef(rivalAngleX, 1, 0, 0);
	glRotatef(rivalAngleY, 0, 1, 0);

	glColor3f(1, 1, 1);
	for (int i = 0; i < X+1; i++) {
		glBegin(GL_LINES);
			glVertex3f(-X / 2 + i, -Y / 2, -Z / 2);
			glVertex3f(-X / 2 + i, -Y / 2, Z / 2);
		glEnd();
		glBegin(GL_LINES);
			glVertex3f(-X / 2, -Y / 2, -Z / 2 + i);
			glVertex3f(X / 2, -Y / 2, -Z / 2 + i);
		glEnd();
	}

	glPushMatrix();
		glTranslatef(-X / 2, -Y / 2, -Z / 2);
		glColor3f(0.7, 0.7, 0.7);
		glBegin(GL_POLYGON);
			glVertex3f(0, -0.01, 0);
			glVertex3f(0, -0.01, Z);
			glVertex3f(X, -0.01, Z);
			glVertex3f(X, -0.01, 0);
		glEnd();

		glPushMatrix();
			glTranslatef(0.5, 0.5, 0.5);
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						if (rivalBlock[i][j][k] != 0) {
							glPushMatrix();
								switch (rivalBlock[i][j][k]) {
								case 1:
									glColor3f(1, 0.5, 0);
									break;
								case 2:
									glColor3f(0, 1, 0);
									break;
								case 3:
									glColor3f(0, 0, 1);
									break;
								case 4:
									glColor3f(1, 1, 0);
									break;
								case 5:
									glColor3f(1, 0, 0);
									break;
								default:
									glColor3f(0, 0, 0);
									break;
								}
								glTranslated(i, j, k);
								glutSolidCube(0.9);
							glPopMatrix();
						}
					}
				}
			}
		glPopMatrix();
	glPopMatrix();
	/* end rival user */

	/**************************************************************************/

	/* current user */
	glViewport(width / 2, 0, width / 2, height);
	// change matrix mode to model view mode
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	gluLookAt(0, 0, Y + 3, 0, 0, -1, 0, 1, 0);

	// for rotating x-axis or y-axis
	glRotatef(myAngleX, 1, 0, 0);
	glRotatef(myAngleY, 0, 1, 0);

	glColor3f(1, 1, 1);
	for (int i = 0; i < X + 1; i++) {
		glBegin(GL_LINES);
			glVertex3f(-X / 2 + i, -Y / 2, -Z / 2);
			glVertex3f(-X / 2 + i, -Y / 2, Z / 2);
		glEnd();
		glBegin(GL_LINES);
			glVertex3f(-X / 2, -Y / 2, -Z / 2 + i);
			glVertex3f(X / 2, -Y / 2, -Z / 2 + i);
		glEnd();
	}

	glPushMatrix();
		glTranslatef(-X / 2, -Y / 2, -Z / 2);
		glColor3f(0.7, 0.7, 0.7);
		glBegin(GL_POLYGON);
			glVertex3f(0, -0.01, 0);
			glVertex3f(0, -0.01, Z);
			glVertex3f(X, -0.01, Z);
			glVertex3f(X, -0.01, 0);
		glEnd();

		glPushMatrix();
			glTranslatef(0.5, 0.5, 0.5);
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++) {
						if (myBlock[i][j][k] != 0) {
							glPushMatrix();
								switch (myBlock[i][j][k]) {
								case 1:
									glColor3f(1, 0.5, 0);
									break;
								case 2:
									glColor3f(0, 1, 0);
									break;
								case 3:
									glColor3f(0, 0, 1);
									break;
								case 4:
									glColor3f(1, 1, 0);
									break;
								case 5:
									glColor3f(1, 0, 0);
									break;
								default:
									glColor3f(0, 0, 0);
									break;
								}
								glTranslated(i, j, k);
								glutSolidCube(0.9);
							glPopMatrix();
						}
					}
				}
			}
			switch (currentBlock[8][0]) {
			case 1:
				glColor3f(1, 0.5, 0);
				break;
			case 2:
				glColor3f(0, 1, 0);
				break;
			case 3:
				glColor3f(0, 0, 1);
				break;
			case 4:
				glColor3f(1, 1, 0);
				break;
			case 5:
				glColor3f(1, 0, 0);
				break;
			default:
				break;
			}
			for (int i = 0; i < 8; i++) {
				if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
					glPushMatrix();
						glTranslated(currentBlock[i][0], currentBlock[i][1], currentBlock[i][2]);
						glutSolidCube(0.9);
					glPopMatrix();
				}
			}
		glPopMatrix();
	glPopMatrix();
	/* end current user */

	glFlush();
}

// the function for click event of a mouse
void MyMouse(int button, int state, int x, int y) {
	// store the mouse position when left button is clicked
	if ((button == GLUT_LEFT_BUTTON) && (state == GLUT_DOWN)) {
		firstX = mouseX = (GLfloat)x;
		firstY = mouseY = (GLfloat)y;
	}
}

// the function for mouse motion event
void MyMotion(int x, int y) {
	if (x > 0 && x < width && y > 0 && y < height) { // this function executes only if the mouse is in the window
		if (x > width / 2) {
			if ((x > mouseX) && (x > firstX)) // if x has more than previous value(mouseX) when x is bigger than firstX
				myAngleY += 5 * (x - firstX) / width; // increase the angle of y-axis
			else if ((x < mouseX) && (x > firstX)) // if x has less than previous value(mouseX) when x is bigger than firstX
				myAngleY -= 5 * (x - firstX) / width; // decrease the angle of y-axis
			else if ((x < mouseX) && (x < firstX)) // if x has less than previous value(mouseX) when x is smaller than firstX
				myAngleY -= 5 * (firstX - x) / width; // decrease angle of y-axis
			else if ((x > mouseX) && (x < firstX)) // if x has more than previous value(mouseX) when x is smaller than firstX
				myAngleY += 5 * (firstX - x) / width; // increase angle of y-axis

			if ((y > mouseY) && (y > firstY)) // same way
				myAngleX += 5 * (y - firstY) / height;
			else if ((y < mouseY) && (y > firstY))
				myAngleX -= 5 * (y - firstY) / height;
			else if ((y < mouseY) && (y < firstY))
				myAngleX -= 5 * (firstY - y) / height;
			else if ((y > mouseY) && (y < firstY))
				myAngleX += 5 * (firstY - y) / height;
		}
		else {
			if ((x > mouseX) && (x > firstX)) // if x has more than previous value(mouseX) when x is bigger than firstX
				rivalAngleY += 5 * (x - firstX) / width; // increase the angle of y-axis
			else if ((x < mouseX) && (x > firstX)) // if x has less than previous value(mouseX) when x is bigger than firstX
				rivalAngleY -= 5 * (x - firstX) / width; // decrease the angle of y-axis
			else if ((x < mouseX) && (x < firstX)) // if x has less than previous value(mouseX) when x is smaller than firstX
				rivalAngleY -= 5 * (firstX - x) / width; // decrease angle of y-axis
			else if ((x > mouseX) && (x < firstX)) // if x has more than previous value(mouseX) when x is smaller than firstX
				rivalAngleY += 5 * (firstX - x) / width; // increase angle of y-axis

			if ((y > mouseY) && (y > firstY)) // same way
				rivalAngleX += 5 * (y - firstY) / height;
			else if ((y < mouseY) && (y > firstY))
				rivalAngleX -= 5 * (y - firstY) / height;
			else if ((y < mouseY) && (y < firstY))
				rivalAngleX -= 5 * (firstY - y) / height;
			else if ((y > mouseY) && (y < firstY))
				rivalAngleX += 5 * (firstY - y) / height;
		}
		mouseX = x;
		mouseY = y;
		glutPostRedisplay();
	}
}

// reshaping for the changed window size or moving
void MyReshape(int w, int h) {
	widthFactor = (GLfloat)w / 1000;
	heightFactor = (GLfloat)h / 700;
	width = w;
	height = h;
}

void MyMainMenu(int EntryID) {
	//
}

// the function for special key event
void MySpecialKey(int key, int x, int y) {
	if ((key == GLUT_KEY_LEFT) && (glutGetModifiers() != GLUT_ACTIVE_CTRL))
		moveBlock(0);
	else if ((key == GLUT_KEY_RIGHT) && (glutGetModifiers() != GLUT_ACTIVE_CTRL))
		moveBlock(1);
	else if ((key == GLUT_KEY_UP) && (glutGetModifiers() != GLUT_ACTIVE_CTRL))
		moveBlock(3);
	else if ((key == GLUT_KEY_DOWN) && (glutGetModifiers() != GLUT_ACTIVE_CTRL))
		moveBlock(4);
	else if ((key == GLUT_KEY_LEFT) && (glutGetModifiers() == GLUT_ACTIVE_CTRL))
		changeBlock(0);
	else if ((key == GLUT_KEY_RIGHT) && (glutGetModifiers() == GLUT_ACTIVE_CTRL))
		changeBlock(1);
	else if ((key == GLUT_KEY_UP) && (glutGetModifiers() == GLUT_ACTIVE_CTRL))
		changeBlock(2);
	else if ((key == GLUT_KEY_DOWN) && (glutGetModifiers() == GLUT_ACTIVE_CTRL))
		changeBlock(3);

	glutPostRedisplay();
}

void MyTimer(int value) {
	moveBlock(2);
	glutPostRedisplay();
	glutTimerFunc(1000, MyTimer, 1);
}

void sendBlock() {
	int count = 0;
	char buffer[X*Y*Z];
	int temp[X][Y][Z] = { 0 };
	memset(buffer, '\0', sizeof(buffer));
	//memset(temp, 0, sizeof(temp));
	for (int i = 0; i < X; i++) {
		for (int j = 0; j < Y; j++) {
			for (int k = 0; k < Z; k++) {
				temp[i][j][k] = 0;
			}
		}
	}
	for (int i = 0; i < 8; i++) {
		if (currentBlock[i][0] != -1 && currentBlock[i][1] != -1 && currentBlock[i][2] != -1) {
			temp[currentBlock[i][0]][currentBlock[i][1]][currentBlock[i][2]] = currentBlock[8][0];
		}
	}
	for (int i = 0; i < X; i++) {
		for (int j = 0; j < Y; j++) {
			for (int k = 0; k < Z; k++) {
				temp[i][j][k] += myBlock[i][j][k];
			}
		}
	}
	send(fd, (char*)temp, sizeof(temp), 0);
}

void getRivalBlock() {
	while (1) {
		recv(fd, (char*)rivalBlock, sizeof(rivalBlock), 0);
		glutPostRedisplay();
	}
}

int main(int argc, char **argv) {
	
	int result, length;
	WSADATA wsa;
	struct sockaddr_in address;
	char buffer[X*Y*Z];

	result = WSAStartup(MAKEWORD(2, 2), &wsa);
	if (result != 0) {
		perror("WSA failed");
		exit(EXIT_FAILURE);
	}

	fd = socket(AF_INET, SOCK_STREAM, 0);
	address.sin_family = AF_INET;
	address.sin_addr.s_addr = inet_addr("127.0.0.1");
	address.sin_port = htons(9734);
	length = sizeof(address);

	result = connect(fd, (struct sockaddr *)&address, length);
	if (result == -1) {
		perror("Connection failed");
		exit(EXIT_FAILURE);
	}
	
	printf("New ID : ");
	do {
		memset(buffer, '\0', sizeof(buffer));
		fgets(buffer, sizeof(buffer), stdin);
		buffer[(int)strlen(buffer) - 1] = '\0';
		//write(sock, buffer, strlen(buffer));
		send(fd, buffer, sizeof(buffer), 0);
		//result = read(sock, buffer, sizeof(buffer));
		result = recv(fd, buffer, sizeof(buffer), 0);
		if (result < 1) {
			printf("Server down\n");
			exit(EXIT_FAILURE);
		}
		if (!strcmp(buffer, "yang-3521")) {
			printf("Creating id succeeded\n");
			break;
		}
		else {
			printf("Your id is duplicated. Input again : ");
		}
	} while (1);

	while (1) {
		//result = read(sock, buffer, sizeof(buffer));
		result = recv(fd, buffer, sizeof(buffer), 0);
		if (result < 1) {
			printf("Server down\n");
			exit(EXIT_FAILURE);
		}
		if (!strcmp(buffer, "start")) {
			break;
		}
		else {
			printf("Wait rival user");
		}
	}

	std::thread thread1(getRivalBlock);

	// initializing
	width = 1000.0;
	height = 700.0;
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(width, height);
	glutInitWindowPosition(100, 10);
	glutCreateWindow("3D Tetris");
	glClearColor(0, 0, 0, 1.0);
	memset(myBlock, 0, sizeof(myBlock));
	memset(currentBlock, 0, sizeof(currentBlock));
	memset(rivalBlock, 0, sizeof(rivalBlock));
	
	createBlock();
	
	glutKeyboardFunc(MyKeyboard);
	glutMotionFunc(MyMotion);
	glutMouseFunc(MyMouse);
	glutReshapeFunc(MyReshape);
	glutDisplayFunc(MyDisplay);
	glutSpecialFunc(MySpecialKey);
	glutTimerFunc(1000, MyTimer, 1);
	glutMainLoop();
	thread1.join();
	closesocket(fd);
	WSACleanup();
	return 0;
}