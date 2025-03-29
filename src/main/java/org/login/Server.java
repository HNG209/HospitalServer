package org.login;

import org.login.dao.DoctorDao;
import org.login.entity.Doctor;
import org.login.utils.AppUtils;
import org.neo4j.driver.Driver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(2909)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("host:" + socket.getInetAddress().getHostName());
                System.out.println("port:" + socket.getPort());

                Thread thread = new Thread(new HandlingSocket(socket));
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class HandlingSocket implements Runnable {
    private Socket socket;
    private DoctorDao doctorDao;

    public HandlingSocket(Socket socket) {
        Driver driver = AppUtils.getDriver();
        String dbName = "hng";
        doctorDao = new DoctorDao(driver, dbName);
        this.socket = socket;
    }

    @Override
    public void run() {
        try(DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in)
        ) {
            while(true) {
                String msg = "From server\n" +
                        "Choose your option:\n" +
                        "1) Add new doctor\n" +
                        "2) Count doctor by department name\n" +
                        "3) Find doctor by speciality\n" +
                        "4) Update diagnosis\n" +
                        "Enter your option (1-4):";

                outputStream.writeUTF(msg);
                outputStream.flush();
                int option = inputStream.readInt();

                switch (option) {
                    case 1:{
                        Doctor doctor = (Doctor) objectInputStream.readObject();

                        //reply
                        String reply = doctorDao.add(doctor) ? "Added successfully" : "Added failed";
                        outputStream.writeUTF(reply);
                        break;
                    }
                    case 2: {
                        String deptName = inputStream.readUTF();

                        objectOutputStream.writeObject(doctorDao.countDoctorBy(deptName));
                        break;
                    }
                    case 3: {
                        String speciality = inputStream.readUTF();

                        objectOutputStream.writeObject(doctorDao.findDoctorBy(speciality));
                        break;
                    }
                    case 4: {
                        String doctorID = inputStream.readUTF();
                        String patientID = inputStream.readUTF();
                        String newDiagnosis = inputStream.readUTF();

                        String reply = doctorDao.updateDiagnosis(patientID, doctorID, newDiagnosis) ? "Updated successfully" : "No update was made";
                        outputStream.writeUTF(reply);
                        break;
                    }
                    default:
                        outputStream.writeUTF("option not valid!");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
