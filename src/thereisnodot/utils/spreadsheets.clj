;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leahcim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-10-20 20:31 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@

(ns
    ^{:doc "Save and load collections to Excel"
      :author "Michael Leahcim"}
    thereisnodot.utils.spreadsheets
  (:require [dk.ative.docjure.spreadsheet :as xlsx]
            [thereisnodot.akronim.core :refer [defns defns-]]
            [thereisnodot.utils.fs :as fs-utils]
            [clojure.string :as string]))

(def ^{:private true
       :doc "Generates 26 x 26 sequence of excel alphabet From :A till :ZZ"} accessors
  (let [accessors-alphabet [:A :B :C :D :E :F :G :H :I :J :K :L :M :N :O :P :Q :R :S :T :U :V :W :X :Y :Z]]
    (->>
     (keyword (str (name i) (name j)))
     (for [j accessors-alphabet])
     (for [i accessors-alphabet])
     (cons accessors-alphabet)
     (flatten))))

(defn- get-key-names
  [sheet]
  (->>
   (xlsx/row-seq sheet)
   first
   (xlsx/cell-seq)
   (map xlsx/read-cell)
   (into [])))

(defns- map-on-vec
  "Will map on vector"
  [(map-on-vec
    ["a" "b" "n"]
    {"n" 12 "q" 10}
    "n/a") => (list "n/a" "n/a" 12)]
  [some-vector some-map or]
  (for [item some-vector]
    (get some-map item or)))

(defn- extract-keys
  "Will extract keys from multple maps while preserving order, if exists.
   See tests for samples"
  [datum order]
  (let [ordered (into {} (map vector order   (range)))]
    (->>
     (distinct (flatten (map keys datum)))
     (sort-by (fn [item] (get ordered item (inc (count datum))))))))

(defn- maps->header-and-rows
  "Will prepare a list of maps to insert to a spreadsheet. 
   See tests for samples"
  [maps if-empty header-order]
  (let [keyseq (extract-keys maps header-order)]
    {:header (into [] keyseq)
     :rows
     (for [map-row maps]
       (map-on-vec keyseq map-row if-empty))}))

(defn- maps->justified-lists
  "Same as maps->header-and-rows, but outputs an array where the first row is the header row"
  [maps if-empty header-order]
  (let [{header :header rows :rows } (maps->header-and-rows maps if-empty header-order)]
    (concat [(map name header)] rows)))

(defn- excel-spitter
  [fpath sheet-maps if-empty header-order]
  (xlsx/save-workbook!
   (str fpath)
   (apply xlsx/create-workbook
          (apply concat
                 (for [[sheet-name sheet-items] sheet-maps]
                   [(name  sheet-name) (maps->justified-lists sheet-items if-empty header-order)])))))

(defns excel-spit
  "Will turn maps into a spreadsheet of many sheets
   will overwrite any file that is on the given `fpath`

   `if-empty` should be a string value that will replace 
   empty cell. 

   `Header order` will put order on the fields. 
   Those which specified, will go first in the specified order.

   See tests for more examples"
  [(excel-spit
    "/tmp/thereisnodot.utils7945546543919997425.xlsx"
    {"sheet1" (list {:first_column "a1" :secon_column "no one" :third_column "works on this"}
                    {                   :secon_column "no one"                              })
     "sheet2" (list {                   :secon_column "everything" }
                    {:first_column "b1" :secon_column "no one"     :third_column "works on this"})}
    "n/a" []) => nil

   (excel-spit
    "/tmp/thereisnodot.utils7945546543919997426.xlsx"
    {"sheet1" (list {:first_column "a1" :secon_column "no one" :third_column "works on this"}
                    {                   :secon_column "no one"                              })
     "sheet2" (list {                   :secon_column "everything" }
                    {:first_column "b1" :secon_column "no one"     :third_column "works on this"})}
    "n/a" [:secon_column :third_column]) => nil]
  ([fpath sheet-maps]
   (excel-spit fpath sheet-maps "n/a"))
  ([fpath sheet-maps if-empty]
   (excel-spit fpath sheet-maps if-empty []))
  ([fpath sheet-maps if-empty header-order]
   (excel-spitter  fpath sheet-maps if-empty header-order)))

(defns excel-slurp
  "Load spreadsheet as a hashmap where the key is a sheet name. and value is 
   a list of hashmaps with `:key` being the header of a sheet and `:val` being 
   the value on a given row. 
   Will raise an error if there is no such file
   For dumping data to an excel file see [excel-spit](#excel-spit)
   See tests for more examples"
  [(excel-slurp (str (clojure.java.io/file (clojure.java.io/resource "demo2.xlsx")))) => 
   {"sheet1" (list {:secon_column "no one", :third_column "works on this", :first_column "a1"}
                   {:secon_column "no one", :third_column "n/a", :first_column "n/a"})
    "sheet2" (list {:secon_column "everything", :third_column "n/a", :first_column "n/a"}
                   {:secon_column "no one", :third_column "works on this", :first_column "b1"})}]
  [fname-str]
  (into
   {}
   (for [sheet (xlsx/sheet-seq (xlsx/load-workbook  fname-str))]
     [(xlsx/sheet-name sheet) (rest (xlsx/select-columns (zipmap accessors (map keyword (get-key-names sheet))) sheet))])))

(defns excel-round-down
  "Excel compatible ROUND formula"
  [(excel-round-down 3.2 0) => 3.0
   (excel-round-down 76.9 0) => 76.0
   (excel-round-down 3.14159   3) =>  3.141
   (excel-round-down -3.14159, 1) => -3.1
   (excel-round-down 31415.92654, -2) => 31400.0]
  [number digits]
  (let [sign (if (>  number 0) 1.0 -1.0 )]
    (* sign (/  (Math/floor (* (Math/abs number) (Math/pow 10 digits))) (Math/pow 10 digits)))))

(defns excel-round-up
  "Excel compatible ROUND formula"
  [(excel-round-up 3.2, 0) => 4.0
   (excel-round-up 76.9, 0) => 77.0
   (excel-round-up 3.14159, 3) => 3.142
   (excel-round-up -3.14159, 1) => -3.2
   (excel-round-up 31415.92654, -2) => 31500.0
   (excel-round-up 100.999, -2) => 200.0]
  [number digits]
  (let [sign (if (>  number 0) 1.0 -1.0 )]
    (* sign (/  (Math/ceil (* (Math/abs number) (Math/pow 10 digits))) (Math/pow 10 digits)))))
