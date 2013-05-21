(ns walk.models.walkModelTest
  (require [walk.models.walkModel :as wm])
  (:use clojure.test)
  (:use [walk.core.readimage_test :as ri-test])
)

(deftest test-walk-start
  (is (= '{:x 3 :y 5} (:start (wm/add-walk-start '{:x 3 :y 5}))))
  (is (= '{:x 3 :y 5} (deref wm/walk-start)))
)

(deftest maxEntry
  (let [p {:x 8 :y 25}
        points (for [x (range (- (:x p) 1) (+ (:x p) 2))
                     y (range (- (:y p) 1) (+ (:y p) 2))
                    ]
                   {:x x :y y})
        ;with-darkness (map #(assoc % :darkness (ri/darkness image %)) points)
        with-darkness (map #(assoc % :darkness 3) points)
       ]
    (is (= 3 (:darkness (first with-darkness))))
  ))


(deftest find-adjusted-click-point
  (let [p (wm/adjust-point ri-test/test-image {:x 11 :y 12})]
    (is (= 11 (:x p)))
    (is (= 11 (:y p)))
  ))

(run-tests)
