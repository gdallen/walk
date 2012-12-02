(ns walk.models.walkModel
  (require [walk.core.readimage :as ri]
           [walk.core.walk :as walk]
  )
)


(def default-state :points)
(def current-state (ref default-state))
(def current-unit-of-measure (ref "miles"))

(defn point [x y]
  {:x x :y y})

(def measure-start (ref (point 0 0)))
(def measure-state (ref :none))
(def measure-end (ref (point 0 0)))
(def measure-scale-distance (ref 0))
(def measured-input-distance (ref 0))

(def walk-start (ref (point 0 0)))
(def walk-state (ref :none))
(def walk-end (ref (point 0 0)))
(def walk-distance (ref 0))

(defn change-mode [v]
  (cond
    (= "points" v)  (dosync (ref-set current-state :points))
    (= "measure" v) (dosync (ref-set current-state :measure)
                            (ref-set measure-state :none))
    (= "walk" v)    (dosync (ref-set current-state :walk)
                            (ref-set walk-state :none))
  )
  (deref current-state)
)

(defn change-unit-of-measure [v]
  (dosync (ref-set current-unit-of-measure v))
  v
)


(defn points [p]
  {:state (deref current-state) :point p})

(defn add-measure-start [p]
  (dosync (ref-set measure-state :start))
  (dosync (ref-set measure-start p))
  {:start p}
)  

(defn measure-distance []
  (let [xs (:x (deref measure-start))
        ys (:y (deref measure-start))
        xe (:x (deref measure-end))
        ye (:y (deref measure-end))
       ]
    (Math/sqrt (+ (* (- xe xs) (- xe xs)) (* (- ye ys) (- ye ys))))
  ))

(defn add-measure-end [p]
  (dosync (ref-set measure-state :end))
  (dosync (ref-set measure-end p))
  (dosync (ref-set measure-scale-distance (measure-distance)))
  {:start (deref measure-start) :end p :distance (measure-distance)}
)


(defn add-walk-start [p]
  (dosync (ref-set walk-state :start))
  (dosync (ref-set walk-start p))
  (dosync (ref-set walk-distance 0))
  {:start p}
)

(defn calculate-unit-distance [d]
  (cond
    (= 0 (deref measure-scale-distance)) d
    :else 
      (* (Long/parseLong (deref measured-input-distance)) (/ d (deref measure-scale-distance))) 
))

(defn update-results [res p]
  (println "walk-distance " (deref walk-distance) " distance " (:distance res))
  (let [old-start (deref walk-start)
        new-total (+ (deref walk-distance) (:distance res))]
    (dosync (ref-set walk-start p))
    (dosync (ref-set walk-distance new-total))

    {:start old-start :end p 
       :distance (:distance res) 
       :total-distance new-total
       :point-list (:point-list res)
       :error (:error res)
       :segment-unit-distance (calculate-unit-distance (:distance res) )
       :total-unit-distance (calculate-unit-distance new-total)
    }
))

(defn add-walk-end [im p]
  (def res (walk/walk im (deref walk-start) p))
  (cond 
    (nil? (:error res)) (update-results res p)
    :else res
))

(defn measure-values [p]
  (cond
    (= :none (deref measure-state)) (add-measure-start p)
    (= :start (deref measure-state)) (add-measure-end p)
  ))
    


(defn measure [p]
  {:state (deref current-state) 
   :point p 
   :measure (measure-values p)}
)

(defn walk-values [image p]
  (cond
    (= :none (deref walk-state)) (add-walk-start p)
    (= :start (deref walk-state)) (add-walk-end image p)
  ))

(defn walk [p]
  (def image (ri/read-image "resources/public/img/maps/testMap.png"))  
  (def walk-result (walk-values image p))
  (def final-result {:state (deref current-state) 
   :point p
   :result walk-result
   :total-distance 3})
  
  final-result
)

(defn add-point [p]
  (cond
    (= :points (deref current-state)) (points p)
    (= :measure (deref current-state)) (measure p)
    (= :walk (deref current-state)) (walk p)
  ))

(defn change-the-measured-distance [v]
    (dosync (ref-set measured-input-distance v))
)
