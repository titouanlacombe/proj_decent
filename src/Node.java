import java.net.*;
import java.util.UUID;

import sim.Controller;
import sim.Token;
import sim.protocol.*;
import utils.*;

public class Node {
    String uuid;
    Controller controller;
    FullAddress nextNodeAddress;
    FullAddress simulatorAddress;

    // Args: manager_ip:manager_port
    public void _main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Error: Invalid number of arguments");
            System.out.println("Usage: java Node manager_ip:manager_port [simulator_ip:simulator_port]");
            return;
        }

        this.uuid = UUID.randomUUID().toString();

        // Get simulator address
        if (args.length > 1) {
            simulatorAddress = FullAddress.fromString(args[1]);
            System.out.println("Got simulator address: " + simulatorAddress);
        }

        // Creating server
        ServerSocket serverSocket = new ServerSocket(0);
        FullAddress myAddress = FullAddress.fromSocket(serverSocket);
        System.out.println("Node started at " + myAddress);

        // Send manager my address
        FullAddress managerAddress = FullAddress.fromString(args[0]);
        Socket socket = new Socket(managerAddress.ip, managerAddress.port);
        System.out.println("Sending my uuid/address to " + managerAddress);
        String message = uuid + " " + myAddress;
        socket.getOutputStream().write(message.getBytes());
        socket.close();

        // Wait for manager to send next node
        System.out.println("Waiting for manager to send next node");
        Socket rep_socket = serverSocket.accept();
        String resp = new String(rep_socket.getInputStream().readAllBytes());
        rep_socket.close();
        this.nextNodeAddress = FullAddress.fromString(resp);
        System.out.println("Received next: " + this.nextNodeAddress + " from manager");

        // Start controller
        System.out.println("Setup complete, starting node");
        this.controller = new Controller();

        // Start server
        while (true) {
            Socket clientSocket = serverSocket.accept();
            boolean exit = this.handleRequest(clientSocket);
            clientSocket.close();

            if (exit) {
                break;
            }
        }
    }

    public boolean handleRequest(Socket clientSocket) throws Exception {
        Request request = Protocol.recv(clientSocket);
        System.out.println("\n[NODE " + uuid + "] " + request);

        switch (request.getCode()) {
            case ExitRequest.CODE:
                return true;
            case ArrivalRequest.CODE:
                controller.arrival();
                break;
            case DepartureRequest.CODE:
                controller.departure();
                break;
            case TokenRequest.CODE:
                tokenRequest((TokenRequest) request);
                break;
            default:
                System.out.println("Error: Invalid request code");
                break;
        }

        return false;
    }

    public void tokenRequest(TokenRequest request) throws Exception {
        Token token = request.token;

        System.out.println("Node got token: " + token);
        System.out.println("Node controller state: " + controller);

        // Run node controller
        controller.run(token);

        // Send new controller state to simulation server
        if (simulatorAddress != null) {
            Protocol.send(simulatorAddress, new SimulationUpdateRequest(uuid, controller));
        }

        // Call next node
        Thread.sleep(100);
        Protocol.send(nextNodeAddress, new TokenRequest(token));
    }

    public static void main(String[] args) {
        Node node = new Node();

        try {
            node._main(args);
        } catch (Exception e) {
            System.out.println("Error in main thread");
            e.printStackTrace();
        }
    }
}
