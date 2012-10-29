(ns walk.models.walk
  (require [walk.models.readimage :as ri])
)
  (import 'java.awt.Color)

(defrecord Point [x y])


(defn points-around [p]
  (cons (Point. (- (:x p) 1) (- (:y p) 1) )
    (cons (Point. (- (:x p) 1) (- (:y p) 0))
      (cons (Point. (- (:x p) 1) (+ (:y p) 1))
        (cons (Point. (- (:x p) 0) (- (:y p) 1))
          (cons (Point. (- (:x p) 0) (+ (:y p) 1))
            (cons (Point. (+ (:x p) 1) (- (:y p) 1))
              (cons (Point. (+ (:x p) 1) (- (:y p) 0))
                (cons (Point. (+ (:x p) 1) (+ (:y p) 1)) () )
              )))))))
)


(defn contains-point [col p]
; not sure that this method is needed.
  (cond 
    (nil? col) false
    (nil? (first col)) false
    :else (or 
            (and (= (:x p) (:x (first col)))) 
            (contains-point (rest col) p))
  ))
  

(defn get-point-color [img point]
  (cond
    (nil? img) (println "######## image was nil")
    (nil? point) (println "####### point was nil")
  )
  (ri/get-rgb-color img (:x point) (:y point)))

(defn get-color-difference [c1 c2]
  (let [color1 (new Color c1)
    color2 (new Color c2)]
  (+ (Math/abs (- (.getRed color1) (.getRed color2)))
     (Math/abs (- (.getGreen color1) (.getGreen color2)))
     (Math/abs (- (.getBlue color1) (.getBlue color2)))))
)




(defn on-path [p sc i] 
  (let [max-difference 50
       image i]
  ;(print-color-diff p)
  (< (get-color-difference sc (get-point-color image p)) max-difference)))
(defn next-points [p sc i]
  ;; #### should add filter for points already processed
  ;; #### may need to save that with state??
  (filter #(on-path % sc i) (points-around p)))


; need to work out the objects needed for doing the walk
; already have start-point
; need and end-point
; need a collection of routes to test from and add the start point to that
;    a route has 
;        a list of points that it has travelled.
;        a total distance
; need a way to find the point in the collection that has the shortest
;    path travelled so far
; for the point selected, find all the next points]
(defrecord Route [distance point-list])
(defn create-route 
  ([p] (Route. 0 (conj [] p)))
  ([p r] (Route. (inc (:distance r)) (conj (:point-list r) p))))

(defn find-shortest-distance [routes]
  (let [min-distance (apply min (map #(:distance %) routes))]
   (println " minimum distance found " min-distance)
   (first (filter #(= min-distance (:distance %)) routes))))

; need a way to build the list of routes
; start with an initial route of the starting point and no distance
;(def start-route (create-route start-point))




; need to create new routes for a list of points.
(defn 
  ^{:doc "build a list of routes with the current route (cr) appended with a point from the new point list (npl)"}
  get-new-routes
   ([cr npl] (map #(create-route % cr) npl)))



(defn at-finish? [p r]
  (and 
    (= (:x p) (:x (last (:point-list r))))
    (= (:y p) (:y (last (:point-list r))))))


; build new routes process takes a point and the list of routes
;  needs to build 
(defn build-new-routes [r p rts sc i]
  (apply conj rts (get-new-routes r (next-points p sc i)))
)

(defn remove-route [r rts]
  (remove #(= r %) rts)
)

; time to define the process to take the image, start point, and end point
; and walk the path to create a route from the start to the end
; 
; let starting route be a new route containing the start point
; loop (recursion)
;   get the route that has travelled the least distance
;   if the route is not found return nil or something
;   if the route is at the end then return the route
;   otherwise get the new routes created by stepping from the end of this route
;   and put them into the list of routes to process.
(defn walk-route [rts i sp ep sc]
  (cond
    (nil? rts) nil
    :else (let [route-to-process (find-shortest-distance rts)
          rest-of-routes (remove-route route-to-process rts)
          point-to-process (last (:point-list route-to-process))]
      (println "     processing point " point-to-process " total routes " (count rts))
      (cond
        (at-finish? ep route-to-process) route-to-process
        :else (walk-route (build-new-routes route-to-process point-to-process rest-of-routes sc i) i sp ep sc))))
)


(defn walk [i sp ep]
  ^{:doc "given an image i, start point sp, end point ep, and start color sc,  calculate a route from the start point to the end point" }
  (let [r (create-route sp)
        image i
        routes (conj [] r)
        start-color (get-point-color image sp) ]
  (println "min distance in the routes " (apply min (map #(:distance %) routes)))
    (walk-route routes image sp ep start-color))
)

