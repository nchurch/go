(ns go.core
  (:refer-clojure :exclude [==])
  (:use clojure.core.logic))

(defn dead-equals [self vert hor]
  (== (vert 0) (vert 1))
  (== (hor 0) (hor 1))
  (== (hor 0) (vert 0))
  (== (vert 0) self))

(defn dead-half-equals [self vert hor]
  (== (vert 0) (vert 1))
  (== (hor 0) (hor 1))
  (== (vert 0) self))

(defn dead-vert-unequal [self vert hor]
  (!= (vert 0) (vert 1))
  (conde [
          (== (vert 0) self)
          (== (vert 1) self)]))

(defn dead-hor-unequal [self vert hor]
  (!= (hor 0) (hor 1))
  (conde [
          (== (hor 0) self)
          (== (hor 1) self)]))

(defn dead [self vert hor]
  (conde
    [
    (dead-equals self vert hor)
    (dead-vert-unequal self vert hor)
    (dead-hor-unequal self vert hor)]))
      
(run* [q]
      (fresh [x y z]
             (dead :b [y x] [z x])
             (== q [x y z])))
       


