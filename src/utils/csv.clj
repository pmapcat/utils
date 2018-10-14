(ns utils.csv)

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn csv-file->json-file [in-fpath out-fpath]
  (with-open [reader (io/reader in-fpath)]
    (spit out-fpath
     (json/write-str
      (csv-data->maps
       (map #(map clojure.string/trim %) (csv/read-csv reader :separator \| :quote \")))))))
