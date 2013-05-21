(ns walk.core.readimage)

(import 'java.io.File)
(import 'javax.imageio.ImageIO)
(import 'java.awt.image.BufferedImage)
(import 'java.awt.Color)

(defn read-image [fname] 
;;  (println "trying to read file " fname)
  (ImageIO/read (new File fname))
)

(defn get-rgb-color [img, x y]
  (.getRGB img x y)) 

(defn darkness [image p]
  (let [color-rgb (get-rgb-color image (:x p) (:y p))
        color2 (new Color color-rgb)]
    (+ (.getRed color2) (.getGreen color2) (.getBlue color2))
  ))

(defn points-around [p]
  (cons {:x (- (:x p) 1) :y (- (:y p) 1) }
    (cons {:x (- (:x p) 1) :y (- (:y p) 0)}
      (cons {:x (- (:x p) 1) :y (+ (:y p) 1)}
        (cons {:x (- (:x p) 0) :y (- (:y p) 1)}
          (cons {:x (- (:x p) 0) :y (+ (:y p) 1)}
            (cons {:x (+ (:x p) 1) :y (- (:y p) 1)}
              (cons {:x (+ (:x p) 1) :y (- (:y p) 0)}
                (cons {:x (+ (:x p) 1) :y (+ (:y p) 1)} () 
              ))))))))
)

;(def img (read-image "test.jpg"))

;(println (.getHeight img))

;; create a new image
;(def image (new BufferedImage 50 40 BufferedImage/TYPE_INT_RGB))
;(def g2d (.createGraphics image))
;(.setColor g2d Color/WHITE)
;(.fillRect g2d 0 0 50 40)
;(.setColor g2d Color/BLACK)
;(.drawLine g2d 10 10 20 20)
;(.drawLine g2d 11 10 21 20)

;(println (.getRGB image 10 10) " at pos 10 10")
;(println (.getRGB image 5 5) " at pos 5 5")

;(ImageIO/write image "jpg" (new File "testout.jpg"))
