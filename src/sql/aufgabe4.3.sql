SELECT kunde.nr AS knr, kunde.name AS kunde, lieferant.nr AS lnr, lieferant.name AS lieferant
FROM kunde
LEFT JOIN lieferant ON lieferant.sperre = kunde.sperre
WHERE kunde.name LIKE 'Rafa%'