#include <iostream>
#include <string>
#include <limits> // Required for clearing input buffer

using namespace std;

#define MAX 5

struct Customer {
    string name;
    string movie;
    int tickets;
};

class Queue {
    Customer arr[MAX];
    int front, rear;

public:
    Queue() {
        front = -1;
        rear = -1;
    }

    bool isFull() {
        // Logical check for Circular Queue fullness
        return (front == 0 && rear == MAX - 1) || (rear == (front - 1) % (MAX - 1));
    }

    bool isEmpty() {
        return (front == -1);
    }

    void enqueue(Customer c) {
        if (isFull()) {
            cout << "Queue is Full! Cannot add more bookings.\n";
            return;
        }
        
        if (front == -1) { // First element
            front = rear = 0;
        } else if (rear == MAX - 1 && front != 0) {
            rear = 0; // Wrap around
        } else {
            rear++;
        }
        
        arr[rear] = c;
        cout << "Booking added for " << c.name << endl;
    }

    void dequeue() {
        if (isEmpty()) {
            cout << "No customers in queue to serve.\n";
            return;
        }

        cout << "Ticket issued to " << arr[front].name 
             << " for movie " << arr[front].movie << endl;

        if (front == rear) { // Only one element was left
            front = rear = -1;
        } else if (front == MAX - 1) {
            front = 0; // Wrap around
        } else {
            front++;
        }
    }

    void display() {
        if (isEmpty()) {
            cout << "Queue is Empty\n";
            return;
        }
        cout << "\n--- Current Bookings ---\n";
        int i = front;
        if (rear >= front) {
            while (i <= rear) {
                cout << arr[i].name << " | " << arr[i].movie << " | " << arr[i].tickets << " tickets\n";
                i++;
            }
        } else {
            while (i < MAX) {
                cout << arr[i].name << " | " << arr[i].movie << " | " << arr[i].tickets << " tickets\n";
                i++;
            }
            i = 0;
            while (i <= rear) {
                cout << arr[i].name << " | " << arr[i].movie << " | " << arr[i].tickets << " tickets\n";
                i++;
            }
        }
    }

    void peek() {
        if (isEmpty()) {
            cout << "Queue is Empty\n";
            return;
        }
        cout << "Next Customer in Line: " << arr[front].name << endl;
    }
};

int main() {
    Queue q;
    int choice;
    Customer c;

    int avengersSeats = 5;
    int pathaanSeats = 5;

    do {
        cout << "\n--- Movie Booking System ---";
        cout << "\n1. Book Ticket\n2. Serve Customer\n3. View Next\n4. Display\n5. Exit\n";
        cout << "Enter choice: ";
        
        // Fix for infinite loop: Check if input is a valid integer
        if (!(cin >> choice)) {
            cout << "Invalid input! Please enter a number.\n";
            cin.clear(); // Clear error flags
            cin.ignore(numeric_limits<streamsize>::max(), '\n'); // Discard buffer
            continue;
        }

        switch (choice) {
            case 1:
                cout << "Enter Name: ";
                cin >> c.name;

                cout << "Select Movie (1. Avengers, 2. Pathaan): ";
                int m;
                if (!(cin >> m)) {
                    cout << "Numeric input only.\n";
                    cin.clear();
                    cin.ignore(numeric_limits<streamsize>::max(), '\n');
                    break;
                }

                if (m == 1) c.movie = "Avengers";
                else if (m == 2) c.movie = "Pathaan";
                else {
                    cout << "Invalid movie choice.\n";
                    break;
                }

                cout << "Enter number of tickets: ";
                cin >> c.tickets;

                if (c.movie == "Avengers") {
                    if (c.tickets <= avengersSeats) {
                        avengersSeats -= c.tickets;
                        q.enqueue(c);
                    } else {
                        cout << "Not enough seats! Only " << avengersSeats << " left.\n";
                    }
                }
                else if (c.movie == "Pathaan") {
                    if (c.tickets <= pathaanSeats) {
                        pathaanSeats -= c.tickets;
                        q.enqueue(c);
                    } else {
                        cout << "Not enough seats! Only " << pathaanSeats << " left.\n";
                    }
                }
                break;

            case 2:
                q.dequeue();
                break;

            case 3:
                q.peek();
                break;

            case 4:
                q.display();
                cout << "\n--- Seats Remaining ---\n";
                cout << "Avengers: " << avengersSeats << "\nPathaan: " << pathaanSeats << endl;
                break;
            
            case 5:
                cout << "Exiting system...\n";
                break;

            default:
                cout << "Please select 1-5.\n";
        }
    } while (choice != 5);

    return 0;
}