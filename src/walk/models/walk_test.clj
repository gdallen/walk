(ns walk.models.walk_test
  (:use clojure.test)
  (:use walk.models.walk))

(deftest test-points-around
  (is (= 8 (.size (points-around {:x 3 :y 4})))))

(def points-list (vec (map #(hash-map :x % :y %) (take 19000 (iterate inc 2))) ))

;(println points-list)

(deftest test-contains-point
  (is (= true (contains-point points-list {:x 4 :y 5}))))

(run-tests)
(println "test")

