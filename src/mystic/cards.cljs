(ns ^:figwheel-always mystic.cards

  (:require
   [rum.core :as rum]
   [mystic.core :as core]
   [mystic.components :as comp])

  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]
   ))

(enable-console-print!)

(defcard basic-mystic-rose
  (comp/basic-mystic-rose core/model))

(defcard mystic-rose
  (comp/mystic-rose-1 core/model))

(defcard mystic-rose
  (comp/mystic-rose-2 core/model))


;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html
