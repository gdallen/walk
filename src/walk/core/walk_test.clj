(ns walk.core.walk_test
  (:use clojure.test)
  (:use walk.core.walk))

(deftest test-points-around
  (is (= 8 (.size (points-around {:x 3 :y 4})))))

(def points-list (vec (map #(hash-map :x % :y %) (take 19000 (iterate inc 2))) ))

;(println points-list)

(deftest test-contains-point
  (is (= true (contains-point points-list {:x 4 :y 4}))))

(deftest test-not-contains-point
  (is (not (= true (contains-point points-list {:x 4 :y 5})))))

(deftest distance-new-route
  (let [r (create-route {:x 3 :y 5})]
    (is (= 0 (:distance r)))
  ))

(deftest distance-route-with-point-1
  (let [r1 (create-route {:x 3 :y 5})
        r (create-route {:x 4 :y 5} r1)
       ]
    (is (= 1.0 (:distance r)))
  ))
(deftest distance-route-with-point-2
  (let [r1 (create-route {:x 3 :y 5})
        r (create-route {:x 4 :y 6} r1)
       ]
    (is (< (Math/abs (- (Math/sqrt 2) (:distance r))) 0.0001))
  ))

(deftest distance-route-with-three-points
  (let [r1 (create-route {:x 3 :y 5})
        r2 (create-route {:x 4 :y 5} r1)
        r (create-route {:x 5 :y 6} r2)
       ]
    (is (< (Math/abs (- (+ 1 (Math/sqrt 2)) (:distance r))) 0.0001))
  ))

(run-tests)
