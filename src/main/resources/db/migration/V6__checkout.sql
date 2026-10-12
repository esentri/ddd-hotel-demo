-- Tag des Checkouts (vorzeitige oder verspaetete Abreise moeglich)
ALTER TABLE buchung ADD COLUMN ausgecheckt_am DATE;
