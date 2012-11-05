(ns walk.views.welcome
  (:require [walk.views.common :as common]
            [noir.content.getting-started]
            [walk.models.walkModel :as walk]
)
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [hiccup.page :only [include-css include-js html5]]
        [hiccup.element :only [javascript-tag]]
        [noir.fetch.remotes :only [defremote]]
))

(defpage "/welcome" []
               (include-css "/css/canvas.css")
         (common/layout
          [:div#header 
           [:p "Welcome to walk"]]
          [:div#main
            [:div#description 
              [:p "Test Map"]
            ]
            [:div#content
              [:table (:style "border: 1; width: 90%")
                [:tr
                  [:td ;;(:style "width: 60%")
                    [:div#map]
                  ]
                  [:td
                    [:div#numbers]
                  ]
                ]
              ]
              [:div#notes
                [:p "Next Up: Code clean up"]
              ]
            ]
          ]
         ))

(defremote the-point [p]
;;  (println "made it to the server")
  ;;{:x (+ 1 (:x p)) :y (+ 1 (:y p)) }
  (def the-result (walk/add-point p))
;;  (println "returning from the server page")
  the-result
)

(defremote the-mode [v]
  (walk/change-mode v)
)

(defremote the-unit-of-measure [v]
  (walk/change-unit-of-measure v)
)

