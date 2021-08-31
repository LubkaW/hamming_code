package com.company;

import  java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)  {

        Scanner sc = new Scanner(System.in);
        System.out.println("Write a mode: ");
        File send = new File("C:\\Users\\kasal\\Desktop\\send.txt");
        File encoded = new File("C:\\Users\\kasal\\Desktop\\encoded.txt");
        File received = new File("C:\\Users\\kasal\\Desktop\\received.txt");
        File decoded = new File("C:\\Users\\kasal\\Desktop\\decoded.txt");

        String decision = sc.nextLine();

    switch (decision) {
        case "encode":
            writeByteData(encoded, encodeByteData(readTextData(send)));
            break;
        case "send":
            writeByteData(received, makeError(readByteData(encoded)));
            break;
        case "decode":
            writeTextData(decoded, decodeByteData(readByteData(received)));
            break;

    }


    }

    public  static  byte[] readByteData(File file){

        try(BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file))){
            byte[] b = new byte[(int)file.length()];
            reader.read(b);
            System.out.println();
            System.out.print("Přečtená encoded data:  ");
            for(byte s : b){
                System.out.print(s+" ");
            }
            return b;

        }catch(IOException e){
            System.out.println(e.getMessage());
        }


       return null;
    }
    public static byte[] readTextData(File file){

        byte[] readedBytes = new byte[(int)file.length()];
        try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))){

            stream.read(readedBytes);

        }catch(IOException e){
            System.out.println(e.getMessage());
        }


        System.out.print("Přečtená data v textu:  ");
        for(byte b : readedBytes){
            System.out.print((char)(b&0xFF));
        }
        System.out.println();
        System.out.print("Přečtená data v bitech: ");
        for(byte b : readedBytes){
            System.out.print(Integer.toBinaryString(b&0xFF)+" ");
        }
        return readedBytes;
    }

    public static int[] decodeByteData(byte[] b){
        int poradi = 0;
        int[] decodedByteArray = new int[b.length/2];
        int[] decodedByte = new int[8];
        int help = 0;


        for(int bytos : b){
            bytos = bytos & 0xFF;
            int mask = 128;
            int[] byteInAr = new int [8];

            for(int i = 1; i < 8;i++){
                if((mask&bytos)>0) byteInAr[i] = 1;
                mask>>=1;
            }

            if(poradi == 0){
                computeInt(byteInAr,poradi,decodedByte);
                poradi++;
                continue;
            }

            computeInt(byteInAr,poradi,decodedByte);
            int decodedIntByte = 0;
            for(int i = 0; i<8;i++){
                decodedIntByte += Math.pow(2,i)*decodedByte[i];
            }
            poradi = 0;
            decodedByteArray[help] = decodedIntByte;
            help++;


        }
        return decodedByteArray;
    }

    public static void computeInt(int[] byteInArr,int poradi,int[] decodedByte){
        int pozOfError = 0;
        if(byteInArr[1] != (byteInArr[3]^byteInArr[5]^byteInArr[7])) pozOfError+=1;
        if(byteInArr[2] != (byteInArr[3]^byteInArr[6]^byteInArr[7])) pozOfError+=2;
        if(byteInArr[4] != (byteInArr[5]^byteInArr[6]^byteInArr[7])) pozOfError+=4;

        byteInArr[pozOfError] ^= 1;

        if(poradi == 0){
            decodedByte[7] = byteInArr[3];
            decodedByte[6] = byteInArr[5];
            decodedByte[5] = byteInArr[6];
            decodedByte[4] = byteInArr[7];
        }else{
            decodedByte[3] = byteInArr[3];
            decodedByte[2] = byteInArr[5];
            decodedByte[1] = byteInArr[6];
            decodedByte[0] = byteInArr[7];
        }

    }
    public static void writeTextData(File file, int[] b){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            System.out.println();
            System.out.print("Zapsaná data:           ");
            for(int c : b){
                System.out.print((char)c);
                writer.write((char)c);
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void writeByteData(File file, int[] b){


        try(BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file))){
            System.out.println();
            System.out.print("Zapsaná data:           ");
            for(int c : b){
                System.out.print(c+" ");
                writer.write(c);
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }


    public static int[] encodeByteData(byte[] bytes){
            System.out.println();
            int[] encodedBytesAr = new int[bytes.length*2];
            int mask = 128;
            int help = 0;
            boolean[] bits = new boolean[4];
            for(byte b : bytes){
                int f = b & 0xFF;
                for (int j = 0; j<2;j++){
                    for(int i = 0;i<4;i++){
                        bits[i] = (mask & f) > 0;
                        mask>>=1;
                    }
                    encodedBytesAr[help] = makeEncodedInt(bits);
                    help++;
                }
                mask = 128;

            }

            return  encodedBytesAr;
    }

    public static int makeEncodedInt(boolean[] bits){
        int[] vyslByte = new int[8];


        if(bits[0]) vyslByte[3] = 1;
        if(bits[1]) vyslByte[5] = 1;
        if(bits[2]) vyslByte[6] = 1;
        if(bits[3]) vyslByte[7] = 1;
/*
        for(int i = 1 ;i<8;i++){
            System.out.print(vyslByte[i]);
        }
 */

        for(int i =0; i<3;i++ ){
            int x = (int)Math.pow(2,i);     //ukazuje na pozici paritního bitu
            for(int j = 1; j<8;j++){
                if(((j>>i)&1) == 1){        //zjištuje na jaké pozici má pořadové číslo v bitu jedničku
                    if(x!=j) vyslByte[x] = vyslByte[x] ^ vyslByte[j];
                }
            }

        }
        int result = 0;

        for(int i = 1; i<8;i++){
            if(vyslByte[i] == 1){
                result += Math.pow(2,8-i);
            }
            System.out.print(vyslByte[i]);
        }
        System.out.print(" ");
        return result;

    }


    public static int[] makeError(byte[] b){

        Random rnd = new Random();
        int[] errorBytes = new int[b.length];
        int pozToChange;
        int mask;
        int help = -1;

        for(int s : b){
            s &= 0xFF;
            pozToChange = rnd.nextInt(8);
            mask = 1<<pozToChange;
            s ^= mask;
            errorBytes[++help] = s;
        }


        return errorBytes;
    }



}
