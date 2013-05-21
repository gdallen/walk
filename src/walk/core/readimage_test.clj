(ns walk.core.readimage_test
  (:use clojure.test)
  (:use walk.core.readimage))

(import 'java.awt.image.BufferedImage)
(import 'java.awt.Color)


(def test-image (new BufferedImage 50 50 BufferedImage/TYPE_INT_RGB))
(def g2d (.createGraphics test-image))
(.setColor g2d Color/WHITE)
(.fillRect g2d 0 0 50 50)
(.setColor g2d Color/BLACK)
(.drawLine g2d 10 10 20 20)

(deftest darkness1
  (is (= 0 (darkness test-image {:x 11 :y 11}))))
(deftest darkness2
  (is (= (* 255 3) (darkness test-image {:x 11 :y 14}))))

;(deftest test-get-color
  ;(is (= 255 (get-rgb-color test-image 12 12))))
;(deftest test-get-color-2
  ;(is (= 255 (get-rgb-color test-image 10 12))))
;(deftest test-get-color-3
  ;(is (= 255 (get-rgb-color test-image 11 12))))


(run-tests)
