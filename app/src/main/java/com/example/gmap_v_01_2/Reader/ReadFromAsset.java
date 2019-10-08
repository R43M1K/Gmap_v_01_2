package com.example.gmap_v_01_2.Reader;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadFromAsset {

        BufferedReader reader = null;
        FindStringAfter findStringAfter = new FindStringAfter();
        FindStringBefore findStringBefore = new FindStringBefore();

        public ArrayList<Double> readLong(Context context) {

            ArrayList<Double> LongArray = new ArrayList<Double>();

            try {
                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open("userinfo.txt");
                InputStreamReader streamReader = new InputStreamReader(is);
                reader = new BufferedReader(streamReader);
                ArrayList<String> newArrayList = new ArrayList<String>();
                String theLine;


                while ((theLine = reader.readLine()) != null) {
                    newArrayList.add(theLine);
                }

                reader.close();

                for (int j = 0; j < newArrayList.size(); j++) {
                    //GET LONGITUDE VALUE
                    String LongValue = findStringBefore.before(newArrayList.get(j), ","); //Find Longitude Value it's before ","
                    double longValued = Double.valueOf(LongValue); // Convert Longitude Value String to Double
                    LongArray.add(longValued); // Add Double value of Longitude to Double ArrayList
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return LongArray;

        }

        public ArrayList<Double> readLat(Context context) {

            ArrayList<Double> LatArray = new ArrayList<Double>();

            try {
                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open("userinfo.txt");
                InputStreamReader streamReader = new InputStreamReader(is);
                reader = new BufferedReader(streamReader);
                ArrayList<String> newArrayList = new ArrayList<String>();
                String theLine;


                while ((theLine = reader.readLine()) != null) {
                    newArrayList.add(theLine);
                }

                reader.close();

                for (int j = 0; j < newArrayList.size(); j++) {
                    //GET LATUTUDE VALUE
                    String valueBetwen = findStringAfter.after(newArrayList.get(j), ","); //Find first "," and give string after
                    String LatValue = findStringBefore.before(valueBetwen, ","); // Find second "," and give string before
                    double latValued = Double.valueOf(LatValue); //Convert Latitude Value String to Double
                    LatArray.add(latValued);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return LatArray;
        }

        public ArrayList<Integer> ReadFollowers(Context context) {

            ArrayList<Integer> FollowersArray = new ArrayList<Integer>();

            try {
                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open("userinfo.txt");
                InputStreamReader streamReader = new InputStreamReader(is);
                reader = new BufferedReader(streamReader);
                ArrayList<String> newArrayList = new ArrayList<String>();
                String theLine;


                while ((theLine = reader.readLine()) != null) {
                    newArrayList.add(theLine);
                }

                reader.close();

                for (int j = 0; j < newArrayList.size(); j++) {
                    //GET LATUTUDE VALUE
                    String valueBetwen = findStringAfter.after(newArrayList.get(j), ","); //Find first "," and give string after
                    ;

                    //GET FOLLOWERS NUMBER
                    String valueBetwen2 = findStringAfter.after(valueBetwen,","); // Find first "," after Latitude, and give string after
                    String FollowersValue = findStringBefore.before(valueBetwen2,","); //Find "," before followers number and give string after
                    int followersValueI = Integer.valueOf(FollowersValue); //Convert followers Value String to Double
                    FollowersArray.add(followersValueI);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return FollowersArray;

        }


        public ArrayList<String> ReadPic(Context context) {

            ArrayList<String> ProfilePicArray = new ArrayList<String>();

            try {
                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open("userinfo.txt");
                InputStreamReader streamReader = new InputStreamReader(is);
                reader = new BufferedReader(streamReader);
                ArrayList<String> newArrayList = new ArrayList<String>();
                String theLine;


                while ((theLine = reader.readLine()) != null) {
                    newArrayList.add(theLine);
                }

                reader.close();

                for (int j = 0; j < newArrayList.size(); j++) {
                    //GET LATUTUDE VALUE
                    String valueBetwen = findStringAfter.after(newArrayList.get(j), ","); //Find first "," and give string after

                    //GET FOLLOWERS NUMBER
                    String valueBetwen2 = findStringAfter.after(valueBetwen,","); // Find first "," after Latitude, and give string after

                    //GET PROFILE PICTURE PATH
                    int commaindex = valueBetwen2.indexOf(",") + 2; // +2 is jumping over "/ " these symbols in String
                    String profilePic = valueBetwen2.substring(commaindex); // Find index of last ","
                    ProfilePicArray.add(profilePic);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ProfilePicArray;

        }
    }
