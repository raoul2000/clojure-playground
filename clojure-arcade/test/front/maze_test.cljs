(ns maze-test
  (:require [cljs.test :refer [deftest is testing are]]
            [maze :as maze]))


(deftest manhattan-distance-test
  (testing "compute manhattan distance"
    (is (= 0
           (maze/manhattan-distance [0 0] [0 0])))
    (is (= 4
           (maze/manhattan-distance [0 0] [2 2])))
    (is (= 4
           (maze/manhattan-distance [0 0] [-2 2])))
    (is (= 5
           (maze/manhattan-distance [0 0] [-2 -3])))
    (is (= 4
           (maze/manhattan-distance [-1 0] [-2 -3])))))


(deftest set-at-position-test
  (testing "set position"
    (let [grid [[0 0 0]
                [0 0 0]
                [0 0 0]]]
      (is (= [["X" 0 0] [0 0 0] [0 0 0]]
             (maze/set-at-position grid [0 0] "X")))
      (is (= [[0 0 "X"] [0 0 0] [0 0 0]]
             (maze/set-at-position grid [2 0] "X")))
      (is (= [[0 0 0] [0 0 0] [0 0 "X"]]
             (maze/set-at-position grid [2 2] "X")))
      (is (= [[0 0 0] [0 "X" 0] [0 0 0]]
             (maze/set-at-position grid [1 1] "X")))))

  (testing "index out of bound throws"
    (let [grid [[0 0 0]
                [0 0 0]
                [0 0 0]]]
      (is (thrown-with-msg? js/Error  #".*Index -1 out of bound"
                            (maze/set-at-position grid [-1 1] "X"))))))

(deftest at-position-test
  (testing "get value at position x,y"
    (let [grid [[:a :b :c]
                [:d :e :f]
                [:g :h :i]]]
      (are [x y] (= x y)
        :a (maze/get-at-position grid [0 0])
        :b (maze/get-at-position grid [1 0])
        :i (maze/get-at-position grid [2 2]))))

  #_(testing "throws when index out of bounds"
      (let [grid [[:a :b :c]
                  [:d :e :f]
                  [:g :h :i]]]
        (is (thrown? js/Error
                     (maze/get-at-position grid [0 3])))
        (is (thrown? js/Error
                     (maze/get-at-position grid [3 0])))
        (is (thrown? js/Error
                     (maze/get-at-position grid [-1 0])))
        (is (thrown? js/Error
                     (maze/get-at-position grid [0 -1])))))

  (testing "returns nil when index out of bounds"
    (let [grid [[:a :b :c]
                [:d :e :f]
                [:g :h :i]]]
      (is (nil?
           (maze/get-at-position grid [0 3])))
      (is (nil?
           (maze/get-at-position grid [3 0])))
      (is (nil?
           (maze/get-at-position grid [-1 0])))
      (is (nil?
           (maze/get-at-position grid [0 -1]))))))

(deftest in-grid?-test
  (testing "test a pos is in grid"
    (let [grid [[0 0 0]
                [0 0 0]
                [0 0 0]]]
      (is (true? (maze/in-grid? grid [0 0])))
      (are [x y] (= x y)
        true  (maze/in-grid? grid [0 0])
        true  (maze/in-grid? grid [2 2])
        false (maze/in-grid? grid [3 2])
        false (maze/in-grid? grid [2 3])
        false (maze/in-grid? grid [-1 0])
        false (maze/in-grid? grid [0 -1])))))

(deftest free-adjacent-positions-test
  (testing "Find possible positions for next move"
    (let [grid [[0 0 0]
                [0 0 0]
                [0 0 0]]
          free-pos? (fn [pos]
                      (zero? (maze/get-at-position grid pos)))
          free-pos2? (fn [pos]
                       (pos-int? (maze/get-at-position grid pos)))]
      (is (= [[1 0] [0 1]]
             (maze/free-adjacent-positions [0 0] free-pos?)))
      (is (= '([2 1] [0 1] [1 2] [1 0])
             (maze/free-adjacent-positions [1 1] free-pos?)))
      (is (= []
             (maze/free-adjacent-positions [1 1] free-pos2?)))))

  (testing "when pos is out of bounds"
    (let [grid [[0 0 0]
                [0 0 0]
                [0 0 0]]
          free-pos? (fn [pos]
                      (zero? (maze/get-at-position grid pos)))]
      (is (= []
             (maze/free-adjacent-positions [3 3] free-pos?)))
      (is (= [[0 1]]
             (maze/free-adjacent-positions [-1 1] free-pos?)))
      (is (= [[1 0]]
             (maze/free-adjacent-positions [1 -1] free-pos?))))))

(deftest find-in-grid-test
  (testing "find first pos of given value in grid"

    (let [grid [[:a :b :c :d]
                [:e :f :g :h]
                [:i :j :k :l]]]
      (are [x y] (= x (maze/find-in-grid grid y))
        [0 0] :a
        [1 0] :b
        [2 0] :c
        [3 0] :d
        [0 1] :e
        [1 1] :f
        [2 1] :g
        [3 1] :h
        [0 2] :i
        [1 2] :j
        [2 2] :k
        [3 2] :l
        nil   :not-found))))

(deftest index->pos-test
  (testing "when success"
    (are [pos index] (= pos (maze/index->pos [[1 2 3 4]
                                              [1 2 3 4]
                                              [1 2 3 4]] index))
      [0 0] 0
      [1 0] 1
      [2 0] 2
      [3 0] 3
      [0 1] 4
      [1 1] 5
      [2 1] 6
      [3 1] 7

      [-1 0] -1)))


