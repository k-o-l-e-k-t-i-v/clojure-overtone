;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone


(defn freq->bpm [hz]
  (* hz 60))
(defn bpm->freq [bpm]
  (/ bpm 60 ))


                                        ;(freq->bpm 120)
                                        ;(bpm->freq 120)
(definst pure
  [freq     200
   flt-acc  400
   flt-t    0.2
   flt-base 200
   amp-t    1
   amp      1
   wob-rate 3
   ]
  (let [amp-env (env-gen (perc 0 amp-t amp 0) 1 1 0 1)
        flt-env (env-gen (perc 0 flt-t flt-acc 0) 1 1 0 1)
        src (square freq)
        wob (lin-exp (sin-osc wob-rate) -1 1 flt-base (+ flt-base flt-acc))
        src (rlpf src (+ wob flt-env))
;        src (normalizer src)
        vib (* 0.003 (lf-noise2:ar 2))
        src (mix [(distort (* 2 src)) (* 0.3 (comb-c src 1 (+ 0.014 vib) 0.9))])
        src (normalizer src)
        src (* amp-env src)
        src (mix [src (* 0.5 (free-verb src 0.96 0.18 0.18))])
        silencio (detect-silence src 0.01 0.5 FREE)
        p   (* 0.3 (lf-noise2:ar 2))]
    (out 0 (pan2 src p))))

(pure :amp-t 1 :freq (midi->hz (note :c1)) :flt-acc 1000 :wob-rate 8 :flt-base 60)

(def notes
  [[:g#1 3]
   [:g#1 3]
   [:g#1 2]
   [:g1  3]
   [:g1  3]
   [:g1  2]
   [:c1  4]
   [:c#1 2]
   [:c#2 1]
   [:a#1 1]
   [:d#1 3]
   [:c2  3]
   [:c#2 2]])



(def step-time
  200)

(defn meloplay
  [t st data]
  (let [d (first data)
        n (midi->hz (note (first d)))
        nt (second d)
        rot (rotate 1 data)]
    (at t (pure :amp-t (* st nt 0.001)
                :flt-acc 1000
                :flt-base 40
                :wob-rate (+ 2 (rand-int 6))
                :freq n))
    (apply-at (+ t (* nt st)) meloplay (+ t (* nt st)) st rot [])
    )
  )
(pure :amp-t (* 200 0.001)
                :flt-acc 1000
                :flt-base 40
                :wob-rate (+ 2 (rand-int 6))
                :freq 200)

(stop)
(meloplay (now) step-time notes)



(defn meloplay-2
  [t st data off r]
  (let [d (first data)
        offf (+ off (choose [0 12 -12]))
        n (midi->hz (+ offf (note (first d))))
        nt (second d)
        rot (rotate (+ 1 (rand-int r)) data)]
    (at t (pure :amp-t (* st nt 0.001)
                :flt-acc 1000
                :flt-base 40
                :wob-rate (+ 2 (rand-int 6))
                :freq n))
    (apply-at (+ t (* nt st)) meloplay-2 (+ t (* nt st)) st rot off r[])
    )
  )
(defn rtmplay-2
  [t st data r]
  (let [d (first data)
        n (first d)
        nt (second d)
        rot (rotate (+ 1 (rand-int r)) data)]
    (at t (drums (rythm-generator n)))
    (apply-at (+ t (* nt st)) rtmplay-2 (+ t (* nt st)) st rot r[])
    )
  )

(stop)

(do
  (meloplay-2 (now) (* 0.5 step-time) notes 0 0)
;  (meloplay-2 (now) (* 1.5 step-time) notes 12 0)
  (rtmplay-2 (now) step-time notes 4))

(stop)

(meloplay-2 (now) step-time notes 0)
(meloplay-2 (now) step-time notes -12)


(rtmplay-2 (now) (/ step-time 2) notes 0)

(defn freq->ms [f]
  (/ 1000 f))

(defn freq->s [f]
  (/ 1 f))

(definst brnk
  [t 1
   f 0]
  (let [s (pulse:ar t)
        s (comb-c:ar s 2 f 0.9)
        f (rlpf s 300 0.4)
        f (normalizer f)
        ]
    (out 0 (pan2 f))))
(brnk :t 1/8 :f (freq->s (midi->hz (note :c2))))
(brnk :t 1/6 :f (freq->s (midi->hz (note :c3))))
(stop)



(def dloop (load-sample "/home/ubu/sound/dloops/SM101_brk_Hopscotch_110bpm.wav"))
(def dloop2 (load-sample "/home/ubu/sound/dloops/SM101_brk_Module 8_160bpm.wav"))
(def dloop3 (load-sample "/home/ubu/sound/dloops/SM101_brk_Back Step_110bpm.wav"))
(sample-player dloop :rate 1)
(sample-info dloop)
(demo 1 (let [du (sampl)
              si (* 44100 du)
              rl (line:ar 0 si du FREE)
              r  (buf-rd:ar 2 dloop rl)]
          (out 0 (pan2 r))))


(def sbuf (buffer-alloc-read "/home/ubu/sound/dloops/SM101_brk_Hopscotch_110bpm.wav"))
;(buf-fr)


(demo 10 (let [sample-frames (buf-frames sbuf)
               sample-dur (/ sample-frames 44100)
               start-pos  (* sample-frames (* 11 1/16))
               end-pos    (* sample-frames (* 11.5 1/16))
               chunk-dur (/ (- end-pos start-pos) 44100)
               rl (line:ar start-pos end-pos chunk-dur FREE)
               r  (buf-rd:ar 2 sbuf rl)
               p (* 0.5 (lf-noise2:ar 1))]
           (out 0 (pan2 r p))))

(definst drum-sampler
  [src 0
   ptch 1
   amp 1
   st 0
   lng 1
   chunks 16]
  (let [
        sample-frames (buf-frames src)
        sample-dur (/ sample-frames 44100)
        start-pos  (* sample-frames (* st (/ 1 chunks)))
        end-pos    (* sample-frames (* (+ st lng) (/ 1 chunks)))
        chunk-dur (/ (- end-pos start-pos) 44100 ptch)
        amp-env (env-gen (perc 0 chunk-dur amp 10) 1 1 0 1 FREE)
        rl (line:ar start-pos end-pos chunk-dur)
        r  (buf-rd:ar 2 src rl)
        r (* r amp-env)
        p (* 0.5 (lf-noise2:ar 1))]
    (out 0 (pan2 r p))))

(definst kick1
  [ptch 1
   amp 1
   lng 1]
  (let [
        sample-frames (buf-frames sbuf)
        sample-dur (/ sample-frames 44100)
        start-pos  (* sample-frames (* 0 1/16))
        end-pos    (* sample-frames (* 3 1/16))
        chunk-dur (/ (- end-pos start-pos) 44100 ptch)
        amp-env (env-gen (perc 0 chunk-dur amp 10) 1 1 0 1 FREE)
        rl (line:ar start-pos end-pos chunk-dur)
        r  (buf-rd:ar 2 sbuf rl)
        r (* r amp-env)
        p (* 0.5 (lf-noise2:ar 1))]
    (out 0 (pan2 r p))))

(definst hat1
  [ptch 1
   amp 1]
  (let [
        sample-frames (buf-frames sbuf)
        sample-dur (/ sample-frames 44100)
        start-pos  (* sample-frames (* 11 1/16))
        end-pos    (* sample-frames (* 12 1/16))
        chunk-dur (/ (- end-pos start-pos) 44100 ptch)
        amp-env (env-gen (perc 0 chunk-dur amp 10) 1 1 0 1 FREE)
        rl (line:ar start-pos end-pos chunk-dur)
        r  (buf-rd:ar 2 sbuf rl)
        r (* r amp-env)
        p (* 0.5 (lf-noise2:ar 1))]
    (out 0 (pan2 r p))))

(snare1)
(snare1 :ptch 0.92)
(kick1  :ptch 1)
(hat1   :ptch 2)

(defn rythm-generator
  [nk]
  (let [chance (rand 100)
        gen    (cond (= nk :g#1) (if (< chance 80)
                                    :bd
                                    :sd)
                     (= nk :g1) (if (< chance 70)
                                  :sd
                                  :bd)
                     (= nk :c1) (cond (> chance 80) :hh
                                      (> chance 30) :bd
                                      (<= chance 30) :sd)
                     :else :hh)]
    gen))


(rythm-generator :g1)

(def glob-pitch (* 1 1.36))

(defn drums
  [k]
  (let [rp (+ 1 (weighted-choose [0 1 -0.5] [0.5 0.35 0.15]))
        l  (+ 2 (* 0.5 (rand-int 4)))]
    (cond (= k :bd ) (drum-sampler  :ptch glob-pitch :st 0 :lng 2 :src dloop3 :amp 1)
          (= k :sd ) (drum-sampler  :ptch glob-pitch :st 9 :lng l :src dloop3 :amp 0.85)
          (= k :hh ) (drum-sampler  :ptch glob-pitch :st 21 :lng (* 2 l) :src dloop3 :chunks 32 :amp 0.7))))

(do
  ;(meloplay-2 (now) (* 1 step-time) notes 0 0)
  ;(meloplay-2 (now) (* 1 step-time) notes 12 0)
  (meloplay-2 (now) (* 0.125 step-time) notes 36 6)
  (rtmplay-2 (now) step-time notes 9)
  )

(stop)
