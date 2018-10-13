;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:04 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns utils.excel
  (:require [dk.ative.docjure.spreadsheet :as xlsx]
            [clojure.string :as string]
            [utils.maps :as maps]))

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
       (maps/map-on-vector keyseq map-row if-empty))}))

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
