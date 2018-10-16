;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-14-10 20:47 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.scale)

(defn scale
  "Will calculate linear scaling (mapping)p
   from one metric into another
   For example
   (map (partial scale 1 5 10 50)  (range 1 6)) =>
   (list 10 20N 30N 40N 50)"
  [A B C D X]
  (+
   (* C (- 1  (/ (- X A) (- B A))))
   (* D (/ (- X A) (- B A)))))

(defn log-scale
  "Will calculate log scaling (mapping)
   from one metric into another
   For example
   (map int (map (partial log-scale 1 5 10 50)  (range 1 6))) => 
   (list 10 14 22 33 49)"
  [x1 x2 y1 y2 x]
  (*
   (/ y1 (Math/exp (* (/ (Math/log (/ y1 y2)) (- x1 x2)) x1)))
   (Math/exp (* (/ (Math/log (/ y1 y2) ) (- x1 x2)) x))))

(defn log-scale-round
  "Will round log scale
   For example:
   (map (partial log-scale-round 1 5 10 50)  (range 1 6)) =>
   (10.0 14.0 22.0 33.0 49.0)"
  [x1 x2 y1 y2 x]
  (let [scaled (log-scale x1 x2 y1 y2 x)]
    (Math/floor
     (cond
       (> scaled y2) y2 (< scaled y1) y1
       :else scaled))))

(defn haversine
  "Will calculate Haversine distance between two points on a shphere
   Correctness checked at: https://www.vcalc.com/wiki/vCalc/Haversine+-+Distance"
  ([lat-1 lng-1 lat-2 lng-2]
   (haversine lat-1 lng-1 lat-2 lng-2 6371))
  ([lat-1 lng-1 lat-2 lng-2 radius]
   (let [phi-lat-1 (Math/toRadians lat-1)
         phi-lat-2 (Math/toRadians lat-2)
         d-lat (Math/toRadians (- lat-2 lat-1))
         d-lon (Math/toRadians (- lng-2 lng-1))
         a (+ (Math/pow (Math/sin (/ d-lat 2)) 2) 
              (* (Math/cos phi-lat-1) (Math/cos phi-lat-2)
                 (Math/pow (Math/sin (/ d-lon 2)) 2)))]
     (* radius 2 (Math/atan2 (Math/sqrt a) (Math/sqrt (- 1 a)))))))

(defn euclidean-distance
  "Will calculate Euclidean distance between two vectors"
  [vec-a vec-b]
  (->>
   (map #(* (- %2 %1) (- %2 %1)) vec-a vec-b)
   (apply +)
   Math/sqrt))

(defn lat-lon->x-y
  "Will calculate X and Y coordinates of a map tile, 
   when given latitude longitude and a zoom level. 
   Example: 
   (lat-lon->tile-x-y 42.14380 41.67810 18) => [161421,97171]
   Which you can check on OpenStreetMap: 
   https://c.tile.openstreetmap.org/18/161421/97171.png"
  [lat-deg lon-deg zoom]
  (let [lat-rad (Math/toRadians lat-deg)
        n (Math/pow 2.0 zoom)]
    [(int (* (/ (+ lon-deg 180.0) 360.0) n))
     (int (* (/ (- 1.0 (/ (Math/log (+ (Math/tan lat-rad) (/ 1 (Math/cos lat-rad)))) Math/PI)) 2.0) n))]))

(defn x-y->lat-lon
  "Will calculate latitude and longitude coordinates of a map tile, 
   when given tile X Y and  a zoom level. 
   Example: 
   (x-y->lat-lon 161421 97171 18)
   => [42.14405981155153 41.678009033203125]
   Which you can check on OpenStreetMap: 
   https://www.openstreetmap.org/#map=18/42.1440/41.6780"  
  [xtile ytile zoom]
  (let [n (Math/pow 2.0 zoom)
        lon-deg (- (*  (/ xtile n) 360.0) 180.0)
        lat-rad (Math/atan (Math/sinh (* (Math/PI) (- 1 (/ (* 2 ytile) n)))))
        lat-deg (Math/toDegrees lat-rad)]
    [lat-deg lon-deg]))

(defn tag-font-normalized
  "Will linearly calculate font size of a tag in a tag cloud. 
   Courtesy of: 
   https://stackoverflow.com/a/3717340/3362518"
  [minimum maximum items-in-the-biggest-tag items]
  (* (/ items items-in-the-biggest-tag) (+ (- maximum minimum) minimum)))

(defn tag-font-log-normalized
  "Will non linearly calculate font size of a tag in a tag cloud"
  [items-in-the-biggest-tag items]
  (+ 1/3 (/ (Math/log items) (Math/log items-in-the-biggest-tag) 1.5)))

(defn golden-ratio
  "Will calculate golden ratio"
  [size step]
  (int
   (cond
     (< 0 step)
     (* size (Math/pow 1.618 step))
     (> 0 step)
     (* size (Math/pow 0.618 (Math/abs step))))))

(defn- iterate-with-meta
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

(defn paginate
  "Will create pagination based on 
   current page and the amount of pages as input" 
  [current last-item]
  (let [delta 2
        left (- current delta)
        right (+ current delta 1)]
    (->>
     (filter #(or (= % 1) (= % last-item) (and (>= % left) (< % right))) (range 1 (inc last-item)))
     (reduce
      (fn [prev next]
        (let [prev-last (last prev)
              collapse?  (and  (number? prev-last) (> (- next prev-last) 1))]
          (cond
            (empty? prev)
            [next]
            collapse?
            (-> prev
                (conj "...")
                (conj next))
            :else
            (conj prev next)))) [])
     (iterate-with-meta)
     (map (fn [[point meta]]
            (->
             (if-not (= point "...")
               {:page-num point   :name (str point) :cur? (= point current) }
               {:page-num nil     :name "..."       :cur?  false            })
             (assoc :first? (:first? meta) :last? (:last? meta))))))))
