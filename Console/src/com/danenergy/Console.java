package com.danenergy;

import com.danenergy.common.parser.GenericParser;
import com.danenergy.common.protocol.Command;
import com.danenergy.common.protocol.FrameFormat;
import com.danenergy.common.protocol.Version;

import java.util.Scanner;


public class Console {

    public static void main(String[] args)
    {
        String menu = getMenu();
        SerialComm  serialComm = null;
        int choose=0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bms Command Console");

        do {
            System.out.println(menu);
            System.out.println("Enter command:");
            choose = scanner.nextInt();

            switch (choose)
            {
                case 1:
                    System.out.println("Please choose COM port:");
                    serialComm = new SerialComm();
                    String[] availPorts = serialComm.getAvailablePorts();
                    for(int i=0; i < availPorts.length ; i++)
                    {
                        System.out.println(i+1 + ". " + availPorts[i]);
                    }
                    int commChoice = scanner.nextInt();
                    String selectedPort = availPorts[commChoice-1];
                    serialComm.initializePort(selectedPort,9600,8,1,0);
                    serialComm.open();
                    break;
                case 2:
                    if(null != serialComm)
                    {
                        if(!serialComm.isOpen())
                        {
                            System.out.println("Serial port is closed");
                            break;
                        }

                        System.out.println("Please enter BMS Address:");
                        short address = scanner.nextShort();
                        System.out.println("Please enter BMS command");
                        short command = scanner.nextShort();

                        String cmd = createCommand(address,command);

                        System.out.println("Sending Command : " + cmd);
                        String cmdResponse = serialComm.sendReceive(cmd);
                        System.out.println("Received Response : " + cmdResponse);
                    }
                    else
                    {
                        System.out.println("Please connect to BMS");
                    }
                    break;
                case 9:
                    if(null != serialComm){   serialComm.close();}
                    break;
                default:
                    System.out.println("No such choice");
                    break;

            }

            System.out.println();
        }   while (choose != 9);

    }

    public static String getMenu()
    {
        String menu =
            "1. Connect to BMS\n" +
            "2. Send command to BMS\n" +
            "9. Exit\n";

        return menu;
    }

    public static String createCommand(short address,short command)
    {
        FrameFormat ff = new FrameFormat();
        ff.Address = address;
        ff.Cmd = command;
        ff.Version = (short) Version.Version82.getValue();
        ff.Length = ff.CalculateLength();

        String cmd = GenericParser.Build(ff, FrameFormat.class);

        String cmdSub = cmd.substring(1,cmd.length()-3);

        String crc = FrameFormat.CalculateCRC(cmdSub);

        String finalCmd = FrameFormat.SOI + cmdSub + crc + FrameFormat.EOI;

        return finalCmd;
    }
}
