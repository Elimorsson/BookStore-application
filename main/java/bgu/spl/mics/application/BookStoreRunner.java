package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        if (args.length < 5)
            throw new IllegalArgumentException("please enter at least 5 Arguments!");
        else {
            String JsonName = args[0];
            String CustomerHashMapName = args[1];
            String BooksHashMapName = args[2];
            String OrderReceiptsListName = args[3];
            String MoneyRegisterListName = args[4];


           try {
               Gson gson = new Gson();
                BufferedReader bf = new BufferedReader(new FileReader(JsonName));
               InputGson input = gson.fromJson(bf,InputGson.class);
               ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
               Inventory inventory = Inventory.getInstance();

               DeliveryVehicle[] v =input.initialResources[0].vehicles;
               resourcesHolder.load(v);
               inventory.load(input.initialInventory);
               MoneyRegister moneyRegister = MoneyRegister.getInstance();
               Services services = input.services;
               AtomicInteger canStartTime = new AtomicInteger(0);


               AtomicInteger I = new AtomicInteger();
               AtomicInteger servicesID = new AtomicInteger(1);


               Thread [] ThreadList = new Thread[services.getLength()];
               servicesID.set(1);
               for (int i = 0; i < services.selling; i++){
                   ThreadList[I.get()] = new Thread(new SellingService(servicesID.get(), canStartTime));
                   ThreadList[I.get()].start();
                   I.incrementAndGet();
                   servicesID.incrementAndGet();
               }


               servicesID.set(1);
               for (int i = 0; i < services.inventoryService; i++){
                   ThreadList[I.get()] = new Thread(new InventoryService(servicesID.get(), canStartTime));
                   ThreadList[I.get()].start();
                   I.incrementAndGet();
                   servicesID.incrementAndGet();
               }

               servicesID.set(1);
               for (int i = 0; i < services.logistics; i++){
                   ThreadList[I.get()] = new Thread(new LogisticsService(servicesID.get(), canStartTime));
                   ThreadList[I.get()].start();
                   I.incrementAndGet();
                   servicesID.incrementAndGet();
               }

               servicesID.set(1);
               for (int i = 0; i < services.resourcesService; i++){
                   ThreadList[I.get()] = new Thread(new ResourceService(servicesID.get(), canStartTime));
                   ThreadList[I.get()].start();
                   I.incrementAndGet();
                   servicesID.incrementAndGet();
               }

               servicesID.set(1);
               for (int i = 0; i < services.customers.length; i++) {
                   ThreadList[I.get()] = new Thread(new APIService(servicesID.get(), services.customers[i], canStartTime));
                   ThreadList[I.get()].start();
                   I.incrementAndGet();
                   servicesID.incrementAndGet();
               }
               while(canStartTime.get()<I.get()){}
               ThreadList[I.get()] = new Thread(new TimeService(services.time.speed, services.time.duration));
               ThreadList[I.get()].start();


               for (int i=0;i<ThreadList.length;i++){
                   try {
                       ThreadList[i].join();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }

               HashMap<Integer, Customer> customerHashMap = new HashMap<>();
               Customer [] customersList = services.customers;
               for (int i = 0; i <customersList.length; i++)
                   customerHashMap.put(customersList[i].getId(),customersList[i]);



               inventory.printInventoryToFile(BooksHashMapName);
               moneyRegister.printOrderReceipts(OrderReceiptsListName);

               print(MoneyRegisterListName,MoneyRegister.getInstance());
               print(CustomerHashMapName,customerHashMap);
           }

            catch (FileNotFoundException e){
                System.out.println("the file "+JsonName+" doesn't found");
            }





        }

    }
    public  static void print(String string,Object object){
        try {
            FileOutputStream theFile= new FileOutputStream(string);
            ObjectOutputStream oos = new ObjectOutputStream(theFile);
            oos.writeObject(object);
            oos.close();
            theFile.close();
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

}
