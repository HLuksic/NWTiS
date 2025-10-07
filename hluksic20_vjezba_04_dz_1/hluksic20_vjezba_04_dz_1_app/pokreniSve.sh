#!/bin/bash

echo "Pokrecem CentralniSustav"
java -cp target/hluksic20_vjezba_04_dz_1_app-1.0.0.jar edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.CentralniSustav NWTiS_DZ1_CS.txt

echo "Pokrecem PosluziteljRadara s R1"
java -cp target/hluksic20_vjezba_04_dz_1_app-1.0.0.jar edu.unizg.foi.nwtis.hluksic20.vjezba_04_dz_1.posluzitelji.PosluziteljRadara NWTiS_DZ1_R1.txt

