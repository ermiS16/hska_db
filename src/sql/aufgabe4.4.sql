INSERT INTO kunde(nr, name, strasse, plz, ort, sperre)
VALUES (100, 'Max Mustermann', 'Muserstrasse', 12345, 'Musterstadt', 0);
INSERT INTO auftrag(auftrnr, datum, kundnr, persnr)
VALUES (100, DATE '2019-11-13', 100, (SELECT MAX(persnr) FROM personal));
INSERT INTO auftragsposten
VALUES (1100, 100, (SELECT MAX(teilnr) FROM teilestamm), 20, (SELECT (20*preis) FROM teilestamm WHERE teilnr = 200002)); 
