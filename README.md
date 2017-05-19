# V2X-Simulation-with-SUMO

**Önlab beszámolóm a részletesebb leírásért:** [OneDrive](https://1drv.ms/w/s!AjD91zSjiAZsiIdOOtHze_bp4kE-Xg)

Program grafikus felületén még nem minden funkció működik, hibakezelés sincsen még a különböző funkciókra.
A simulation/ mappa tartalmazza egy SUMO szimulációt.
A saved_maps/ mappa a kiszerializát térképet tartalmazza.
Nyár folyamán szeretném refactorálni a kódot, így a struktúra még változhat.

# Indítás
Program indítását követően meg kell adni a SUMO konfigurációs fájlt, ez a simulation/map.sumo.cfg. Ez betölti a térképet, járműveket. Közben a gpsfake fülön elindítható a gpsfake, a listából ki kell választani a betöltött járművet, majd elindítani a gpsfkaket a "Run gpsfake" gombbal. A szövegdobozba beírt parancs jó a gpsfake elindításához, azt nem szükséges módosítani. Simulation fülön a "Start simulation"-el indítható a szimuláció, ez elindítja a SUMO-t. Csatlakozni a jármű-kommunikációs eszközzel jelenleg a 11111 porton lehet, telnettel ki lehet próbálni. Jelenleg a jármű azonosítóját kell megadni, későbbiekben majd a gpsfake portszámával kell a járműre hivatkozni.
Kiadható parnacsok:
- 	Jármű egy adott pozícióra irányítása: ```555;dst;47.47363;19.05289```
- 	Jármű sebességének megadása (m/s): ```555;speed;10.5 ```


# Vedd figyelembe
- A lejebb linkelt SUMO és gpsfake kell a program működéséhez
- GPSD miatt a program csak Linux alatt működik
- GUI-n a funkciók egy része nem működik, nem erre helyeztem a fókuszt, így nem működnek a következő, de remélem a közeljövőben meg tudom csinálni:
-- gpsfake példányok leállítása, újraindítása
-- Ha betöltöttél egy configurációs fájlt, de másikat szeretnél, akkor indítsd újra a programot, nem írja felül
- Szálak nagyrészére nincs megírva, hogy program leállításkor leálljon (IDE stop gomb)
- gpsfake indítás szintaktikáját és más szintaktikát is tartsd, mert a program nem ellenőrzi

# Külső programok
- SUMO [telepítés](http://www.sumo.dlr.de/userdoc/Installing.html)
- SUMO [wiki](http://sumo.dlr.de/wiki/Simulation_of_Urban_MObility_-_Wiki)
- Módosított [gpsfake](https://github.com/szzso/GPSD-with-management)