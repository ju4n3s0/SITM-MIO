BEGIN {
    FS=","; OFS=","
    print "set search_path to MIO;"
}

NR == 1 { next }  # Saltar la cabecera

{
    # Limpiar comillas si las hubiera
    for (i = 1; i <= NF; i++) {
        gsub(/^"|"$/, "", $i)
    }

    # Construir el INSERT
    printf "INSERT INTO stops (stopid, planversionid, shortname, longname, gps_x, gps_y, decimallongitude, decimallatitude, activationdate, creationdate) VALU
ES (" \
           "%s, %s, '%s', '%s', %s, %s, %s, %s, '%s', '%s');\n", \
           $1, $2, $3, $4, $5, $6, $7, $8, $10, $11
}