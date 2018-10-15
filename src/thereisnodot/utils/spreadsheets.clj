;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:04 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.spreadsheets
  (:require [dk.ative.docjure.spreadsheet :as xlsx]
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

(defn- map-on-vec
  ;; (map-on-vector
  ;;  ["a" "b" "n"]
  ;;  {"n" 12 "q" 10}
  ;;  "n/a")
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


(defn maps->excel-workbook!
  "Will turn maps into a spreadsheet of many sheets."
  [fpath sheet-maps if-empty header-order]
  (xlsx/save-workbook!
   (str fpath)
   (apply xlsx/create-workbook
          (apply concat
                 (for [[sheet-name sheet-items] sheet-maps]
                   [(name  sheet-name) (maps->justified-lists sheet-items if-empty header-order)])))))

(defn excel-workbook->lists
  "Load Sheet of a SpreadSheet as a list of hashmaps. See tests for examples"
  [fname-string]
  (for [sheet (xlsx/sheet-seq (xlsx/load-workbook  fname-string))]
    [(xlsx/sheet-name sheet) (rest (xlsx/select-columns (zipmap accessors (map keyword (get-key-names sheet))) sheet))]))

(defn excel-round-down
  "Excel compatible ROUND formula"
  ;; (= (excel-round-down 3.2 0)  3.0)
  ;; (= (excel-round-down 76.9 0) 76.0)
  ;; (= (excel-round-down 3.14159   3) 3.141)
  ;; (= (excel-round-down -3.14159, 1) -3.1)
  ;; (= (excel-round-down 31415.92654, -2) 31400.0)
  [number digits]
  (let [sign (if (>  number 0) 1.0 -1.0 )]
    (* sign (/  (Math/floor (* (Math/abs number) (Math/pow 10 digits))) (Math/pow 10 digits)))))

(defn excel-round-up
  "Excel compatible ROUND formula"
  ;; (= (excel-round-up 3.2, 0), 4.0)
  ;; (= (excel-round-up 76.9, 0), 77.0)
  ;; (= (excel-round-up 3.14159, 3) 3.142)
  ;; (= (excel-round-up -3.14159, 1), -3.2)
  ;; (= (excel-round-up 31415.92654, -2), 31500.0)
  ;; (= (excel-round-up 100.999, -2), 200.0)
  [number digits]
  (let [sign (if (>  number 0) 1.0 -1.0 )]
    (* sign (/  (Math/ceil (* (Math/abs number) (Math/pow 10 digits))) (Math/pow 10 digits)))))
