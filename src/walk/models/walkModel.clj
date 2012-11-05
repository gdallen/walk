(ns walk.models.walkModel
  (require [walk.models.readimage :as ri]
           [walk.models.walk :as walk]
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

(def walk-start (ref (point 0 0)))
(def walk-state (ref :none))
(def walk-end (ref (point 0 0)))

(defn change-mode [v]
;;  (println (str "change mode method " v))
  (cond
    (= "points" v) (dosync (ref-set current-state :points))
    (= "measure" v) (dosync (ref-set current-state :measure)
                            (ref-set measure-state :none))
    (= "walk" v) (dosync (ref-set current-state :walk)
                         (ref-set walk-state :none))
  )
  (deref current-state)
)

(defn change-unit-of-measure [v]
;;  (println (str "change unit of measure " v))
  (dosync (ref-set current-unit-of-measure v))
  v
)


(defn points [p]
;;  (println "points method")
  {:state (deref current-state) :point p})

(defn add-measure-start [p]
;;  (println "add-measure-start p is " (:x p) ": " (:y p))
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
;;  (println "add-measure end")
  (dosync (ref-set measure-state :end))
  (dosync (ref-set measure-end p))
  {:start (deref measure-start) :end p :distance (measure-distance)}
)


(defn add-walk-start [p]
;;  (println "add-walk-start p is " (:x p) ": " (:y p))
  (dosync (ref-set walk-state :start))
  (dosync (ref-set walk-start p))
  {:start p}
)

(defn add-walk-end [im p]
;;  (println "add walk end p is " (:x p) ": " (:y p))
  (def res (walk/walk im (deref walk-start) p))
;;  (println "result of the walk " res)
  (let [old-start (deref walk-start)]
    (dosync (ref-set walk-start p))
  {:start old-start :end p 
          :distance (:distance res) 
       ;;   :point-list [{:x 3 :y 4}]}
          :point-list (:point-list res)}
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
;;  (println "walk method")
  (def image (ri/read-image "resources/public/img/maps/testMap.png"))  
  (def walk-result (walk-values image p))
;;  (print "walk result " walk-result)
;;  (println "walk state " (deref current-state))
  (def final-result {:state (deref current-state) 
   :point p
   :result walk-result})
  
;;  (println "walk state " (:state final-result))
;;  (println "FINAL RESULT " final-result)
  final-result
)

(defn add-point [p]
  (cond
    (= :points (deref current-state)) (points p)
    (= :measure (deref current-state)) (measure p)
    (= :walk (deref current-state)) (walk p)
  ))

