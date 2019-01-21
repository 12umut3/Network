import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Router extends Thread {
		static ArrayList<Integer> portArr = new ArrayList<Integer>();
		static ArrayList<Integer> socketArr = new ArrayList<Integer>();
		static ArrayList<String > OPSArr = new ArrayList<String>();
		static Socket socket1;
		static Socket socket2;
		public static void main(String [] args){
		int client_port;
		int server_port;
		int numb;
		Semaphore sem = new Semaphore(1);
		//router info
		String str1 = args[0];
		//to do substring to get address and port number of router
		int i = str1.indexOf(":");
		client_port = Integer.parseInt(str1.substring(i+1,args[0].length()));	
		i = 0;
		try {
			ServerSocket serverSocket = new ServerSocket(client_port);
			System.out.println("Router started and listening to the port " + client_port);

			//router is always running
			//read message from client
			socket1 = serverSocket.accept();
			InputStream is = socket1.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String DATA = br.readLine();
			System.out.println("Message received from client is " + DATA);
			//System.out.println(DATA.length());
			String returnMessage;
			//we seperate 8 digit numbers and operators part ****
			try{
				//take ops keys
				int count1 =18;
				int count2 = 18;
				int j = 18;
				while(j <= DATA.length()-1){
					// DATA:00000001 OPS: A,B,C
					while(!DATA.substring(count1,count2+1).equals(",") && !DATA.substring(count1,count2+1).equals("\n")){			
						
						count1++;
						count2++;			
						if(count2 == DATA.length()){
							break;
						}
			
					}
					OPSArr.add(i, DATA.substring(j,count2));			
					j  = count2+1;		
					//System.out.println(OPSArr.size());
					count1++;
					count2++;
					i++;
				}
				/*System.out.println(OPSArr);
				System.out.println(OPSArr.get(0));
				System.out.println(args.length);*/
				//take number part
				returnMessage = DATA.substring(0,13);
			}
			catch(NumberFormatException e){
				//Input data is not a proper version
				returnMessage = "please send proper data";
			}
	        String host = "localhost";
	        InetAddress address = InetAddress.getByName(host);
			//to find port number of operator
			int k = 0;
			for(int l = 0; l < OPSArr.size(); l++){
				for(int j = 0; j < args.length; j++){
					//System.out.println(OPSArr.get(l) +" "+ args[j]);
					if(OPSArr.get(l).equals(args[j])){
						while(!args[j+1].substring(k,k+1).equals(":")) {
							k++;
						}
						sem.acquire();
						//System.out.println("l = " + l + "j =" + j);
						server_port = Integer.parseInt(args[j+1].substring(k+1,args[j+1].length()));		
						socket2 = new Socket(address, server_port);
						//sending message to the server which is getting from client
						OutputStream os = socket2.getOutputStream();
						OutputStreamWriter osw = new OutputStreamWriter(os);
						BufferedWriter bw = new BufferedWriter(osw);

						bw.write(returnMessage);
						bw.flush();
						System.out.println("Message sent to server : " + returnMessage);
						sem.release();
						sem.acquire();
						//Get the return message from the server
						is = socket2.getInputStream();
						isr = new InputStreamReader(is);
						br = new BufferedReader(isr);
						returnMessage = br.readLine();
						System.out.println("Message received from the server : " +returnMessage);
						sem.release();
						break; 
					}
				}
			}
				returnMessage =  returnMessage.substring(5,returnMessage.length());
									
				for(int u = 0; u <= (8 - returnMessage.length()); u++ ){
					returnMessage = "0" + returnMessage;			
				}
				
				returnMessage = "DATA:" + returnMessage;

				//sending message to the client which is getting from server
				OutputStream os = socket1.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				
				bw.write(returnMessage);
				bw.flush();
				System.out.println("Message sent to client : " + returnMessage);

			}
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		    finally
		    {
            try
            {
                socket1.close();
                socket2.close();
            }
            catch(Exception e){
			}
		    }
		}
	}









