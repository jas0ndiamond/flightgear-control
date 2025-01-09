Setup:
1. Download FlightGear-2020.3.17.dmg, FlightGear-2020.3.17-data.txz, FlightGear-2020.3.17-update-data.txz
2. FlightGear-2020.3.17.dmg: Install FlightGear to Desktop or User folder Eg: /Users/<userName>/flightGear/FlightGear.app
3. Extract FlightGear-2020.3.17-data.txz, FlightGear-2020.3.17-update-data.txz through terminal
   - tar -xvf FlightGear-2020.3.17-data.txz
   - tar -xvf FlightGear-2020.3.17-update-data.txz
4. Both data files gets extracted to fgdata directory 
5. Add path of fgfs (Eg: /Users/<userName>/flightGear/FlightGear.app/Contents/MacOS) to $PATH 
6. FG_BIN_PATH : Set the path of fgfs folder to FG_BIN_PATH
7. Run the simulator setup shell script with: flightgear-control/scripts/fg_launcher_mac.sh