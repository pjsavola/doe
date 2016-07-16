#include "../tutorial03_matrices/sd_gl.h"
#include "../tutorial03_matrices/sd_data.h"

#include <iostream>
#include <string>
#include <set>

using namespace std;

glm::vec3 GetCornerCoordinates(float rad) {
	return glm::vec3(cos(rad), sin(rad), 0.0f);
}

GLObjectData CreateHexagon(float radius, const glm::vec3 &color) {
	GLObjectData data;
	data.vertices.reserve(12);
	data.colors.reserve(12);
	data.vertices.push_back(radius * GetCornerCoordinates(PI / 3));
	data.colors.push_back(color * 0.75f);
	data.vertices.push_back(radius * GetCornerCoordinates(2 * PI / 3));
	data.colors.push_back(color * 1.0f);
	data.vertices.push_back(radius * GetCornerCoordinates(PI));
	data.colors.push_back(color * 0.75f);
	data.vertices.push_back(radius * GetCornerCoordinates(PI / 3));
	data.colors.push_back(color * 0.75f);
	data.vertices.push_back(radius * GetCornerCoordinates(PI));
	data.colors.push_back(color * 0.75f);
	data.vertices.push_back(radius * GetCornerCoordinates(4 * PI / 3));
	data.colors.push_back(color * 0.5f);
	data.vertices.push_back(radius * GetCornerCoordinates(PI / 3));
	data.colors.push_back(color * 0.75f);
	data.vertices.push_back(radius * GetCornerCoordinates(4 * PI / 3));
	data.colors.push_back(color * 0.5f);
	data.vertices.push_back(radius * GetCornerCoordinates(0.0f));
	data.colors.push_back(color * 0.5f);
	data.vertices.push_back(radius * GetCornerCoordinates(0.0f));
	data.colors.push_back(color * 0.5f);
	data.vertices.push_back(radius * GetCornerCoordinates(4 * PI / 3));
	data.colors.push_back(color * 0.5f);
	data.vertices.push_back(radius * GetCornerCoordinates(5 * PI / 3));
	data.colors.push_back(color * 0.25f);
	data.color = color;
	return data;
}

class Hex : public GLObject {
public:
	Hex(float radius, const glm::vec3 &color) : GLObject(CreateHexagon(radius, color)) { }
	Hex(float radius, const glm::vec3 &color, bool mid) : GLObject(CreateCircleData(radius, 6, color)) { }
	void Draw(GLuint id, const glm::mat4 &view) const {
		DrawAt(id, glm::translate(glm::vec3(0.0f, 0.0f, 0.0f)), view);
	}
};

class GameData {
public:
	void Init(float r, float b, int w, int h) {
		radius = r;
		border = b;
		width = w;
		height = h;

		land = new Hex(radius, glm::vec3(0.0f, 0.8f, 0.0f));
		plains = new Hex(radius, glm::vec3(0.6f, 0.9f, 0.0f));
		desert = new Hex(radius, glm::vec3(1.0f, 1.0f, 0.0f));
		forest = new Hex(radius, glm::vec3(0.1f, 0.4f, 0.1f));
		water = new Hex(radius, glm::vec3(0.0f, 0.0f, 0.8f));
		mountain = new Hex(radius, glm::vec3(0.8f, 0.8f, 0.8f), true);
		hill = new Hex(radius, glm::vec3(0.7f, 0.7f, 0.3f), true);
	}

	~GameData() {
		delete land;
		delete plains;
		delete desert;
		delete forest;
		delete water;
		delete mountain;
		delete hill;
	}

	void SetMousePosition(double x, double y) {
		mousePosX = x;
		mousePosY = y;
	}

	void MouseClick() const {
		//printf("Click: %f,%f\n", mousePosX, mousePosY);
		glm::vec3 normalized = Normalize(mousePosX, mousePosY);
		//printf("Normalized: %f,%f\n", normalized.x, normalized.y);
		try {
			pair<int, int> p = GetColRow(normalized);
			//printf("Col,Row: %d,%d\n", p.first, p.second);
		}
		catch (int i) {
		}
	}

	glm::vec3 GetPosition(int col, int row) const {
		bool parity = col % 2 == 0;
		float r = radius + border;
		float x = 1.5f * r * col;
		float y = r * sqrt(3) * ((parity ? 0.0f : -0.5f) - row);
		return glm::vec3(x, y, 0.0f);
	}

	const Hex *land;
	const Hex *water;
	const Hex *plains;
	const Hex *mountain;
	const Hex *hill;
	const Hex *forest;
	const Hex *desert;

private:

	glm::vec3 Normalize(double x, double y) const {
		return glm::vec3(float(2 * x / width - 1), float(1 - 2 * y / height), 0.0f);
	}

	pair<int, int> GetColRow(const glm::vec3 &position) const {
		float r = radius + border;
		int col = int(position.x / (1.5f * r));
		int row = int(-position.y / (sqrt(3) * r));
		float best = radius;
		pair<int, int> result;
		for (int i = col - 1; i <= col + 1; i++) {
			for (int j = row - 1; j <= row + 1; j++) {
				const glm::vec3 candPosition = GetPosition(i, j);
				float distance = glm::length(candPosition - position);
				//printf("Dist (%d,%d,%f,%f): %f\n", i, j, candPosition.x, candPosition.y, distance);
				if (distance < best) {
					best = distance;
					result = make_pair(i, j);
				}
			}
		}
		if (best == radius) {
			throw 0;
		}
		return result;
	}

	float radius;
	float border;
	double mousePosX;
	double mousePosY;
	int width;
	int height;
};

GameData gd;

void mouse_button_callback(GLFWwindow *window, int button, int action, int mods) {
	if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS) {
		gd.MouseClick();
	}
}

void cursor_pos_callback(GLFWwindow *window, double xpos, double ypos) {
	gd.SetMousePosition(xpos, ypos);
}

int Program::Run(const char *input) const {

	if (!initialized) {
		return -1;
	}

	glfwSetMouseButtonCallback(window, mouse_button_callback);
	glfwSetCursorPosCallback(window, cursor_pos_callback);

	do {
		glClear(GL_COLOR_BUFFER_BIT);
		shader.Use();
		glBindVertexArray(VertexArrayID);
		gd.land->Draw(MatrixID, glm::translate(gd.GetPosition(0, 0)));
		gd.forest->Draw(MatrixID, glm::translate(gd.GetPosition(1, -1)));
		gd.plains->Draw(MatrixID, glm::translate(gd.GetPosition(-1, -1)));
		gd.desert->Draw(MatrixID, glm::translate(gd.GetPosition(1, 0)));
		gd.hill->Draw(MatrixID, glm::translate(gd.GetPosition(-1, 0)));
		gd.water->Draw(MatrixID, glm::translate(gd.GetPosition(0, -1)));
		gd.mountain->Draw(MatrixID, glm::translate(gd.GetPosition(0, 1)));
		glfwSwapBuffers(window);
		glfwPollEvents();

		if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
			return 0;
		}
	} while (true);
	return 0;
}

int main(int argc, char **argv) {
	Program p(700, 700, "Dawn of Empires");
	gd.Init(0.1f, 0.005f, 700, 700);
	return p.Run(0);
}

