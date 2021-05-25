/* 
 * The MIT License
 *
 * Copyright 2019 The OpenNARS authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nars.entity;

import nars.inference.TruthFunctions;
import nars.io.Symbols;
import nars.language.Term;
import nars.storage.Memory;

/**
 * A Sentence is an abstract class, mainly containing a Term, a TruthValue, and
 * a Stamp.
 * <p>
 * It is used as the premises and conclusions of all inference rules.
 */
public class Sentence implements Cloneable {

    private boolean temporalInduction = false;
    /**
     * The content of a Sentence is a Term
     */
    private Term content;
    /**
     * The punctuation also indicates the type of the Sentence: Judgment,
     * Question, or Goal
     */
    private char punctuation;
    /**
     * The truth value of Judgment
     */
    private TruthValue truth;
    /**
     * Partial record of the derivation path
     */
    private Stamp stamp;
    /**
     * Whether the sentence can be revised
     */
    private boolean revisible;
    
    private boolean observable;

    /**
     * Create a Sentence with the given fields
     *
     * @param content The Term that forms the content of the sentence
     * @param punctuation The punctuation indicating the type of the sentence
     * @param truth The truth value of the sentence, null for question
     * @param stamp The stamp of the sentence indicating its derivation time and
     * base
     */
    public Sentence(Term content, char punctuation, TruthValue truth, Stamp stamp) {
        this.content = content;
        this.content.renameVariables();
        this.punctuation = punctuation;
        this.truth = truth;
        this.stamp = stamp;
        this.revisible = true;
        observable = false;
    }

    /**
     * Create a Sentence with the given fields
     *
     * @param content The Term that forms the content of the sentence
     * @param punctuation The punctuation indicating the type of the sentence
     * @param truth The truth value of the sentence, null for question
     * @param stamp The stamp of the sentence indicating its derivation time and
     * base
     * @param revisible Whether the sentence can be revised
     */
    public Sentence(Term content, char punctuation, TruthValue truth, Stamp stamp, boolean revisible) {
        this.content = content;
        this.content.renameVariables();
        this.punctuation = punctuation;
        this.truth = truth;
        this.stamp = stamp;
        this.revisible = revisible;
        observable = false;
    }

    public boolean getObservable(){
        return observable;
    }
    
    public void setObservable(boolean observable){
        this.observable = observable;
    }
    
    /**
     * To check whether two sentences are equal
     *
     * @param that The other sentence
     * @return Whether the two sentences have the same content
     */
    @Override
    public boolean equals(Object that) {
        if (that instanceof Sentence) {
            Sentence t = (Sentence) that;
            return content.equals(t.getContent()) && punctuation == t.getPunctuation() && truth.equals(t.getTruth()) && stamp.equals(t.getStamp());
        }
        return false;
    }
    
    public int getTemporalOrder(){
        return content.getTemporalOrder();
    }
    
    public long getOccurrenceTime(){
        return stamp.getOccurrenceTime();
    }
    
    public boolean getTemporalInduction(){
        
        return temporalInduction;
        
    }
    
    public void setTemporalInduction(boolean temporalIndution){
        
        this.temporalInduction = temporalInduction;
        
    }

    /**
     * To produce the hashcode of a sentence
     *
     * @return A hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 67 * hash + this.punctuation;
        hash = 67 * hash + (this.truth != null ? this.truth.hashCode() : 0);
        hash = 67 * hash + (this.stamp != null ? this.stamp.hashCode() : 0);
        return hash;
    }

    /**
     * Check whether the judgment is equivalent to another one
     * <p>
     * The two may have different keys
     *
     * @param that The other judgment
     * @return Whether the two are equivalent
     */
    public boolean equivalentTo(Sentence that) {
        assert content.equals(that.getContent()) && punctuation == that.getPunctuation();
        return (truth.equals(that.getTruth()) && stamp.equals(that.getStamp()));
    }

    /**
     * Clone the Sentence
     *
     * @return The clone
     */
    @Override
    public Sentence clone() {
        if (truth == null) {
            return new Sentence((Term) content.clone(), punctuation, null, (Stamp) stamp.clone());
        }
        return new Sentence((Term) content.clone(), punctuation, new TruthValue(truth), (Stamp) stamp.clone(), revisible);
    }

    /**
     * Get the content of the sentence
     *
     * @return The content Term
     */
    public Term getContent() {
        return content;
    }

    /**
     * Get the punctuation of the sentence
     *
     * @return The character '.' or '?'
     */
    public char getPunctuation() {
        return punctuation;
    }

    /**
     * Clone the content of the sentence
     *
     * @return A clone of the content Term
     */
    public Term cloneContent() {
        return (Term) content.clone();
    }

    /**
     * Set the content Term of the Sentence
     *
     * @param t The new content
     */
    public void setContent(Term t) {
        content = t;
    }

    /**
     * Get the truth value of the sentence
     *
     * @return Truth value, null for question
     */
    public TruthValue getTruth() {
        return truth;
    }

    /**
     * Get the stamp of the sentence
     *
     * @return The stamp
     */
    public Stamp getStamp() {
        return stamp;
    }

    public void setStamp(Stamp s) {
        stamp = s;
    }
    
    /**
     * Distinguish Judgment from Goal ("instanceof Judgment" doesn't work)
     *
     * @return Whether the object is a Judgment
     */
    public boolean isJudgment() {
        return (punctuation == Symbols.JUDGMENT_MARK);
    }

    /**
     * Distinguish Question from Quest ("instanceof Question" doesn't work)
     *
     * @return Whether the object is a Question
     */
    public boolean isQuestion() {
        return (punctuation == Symbols.QUESTION_MARK);
    }
    
    public boolean isQuest(){
        return (punctuation == Symbols.QUEST_MARK);                
    }
    
    public boolean isGoal(){
        return (punctuation == Symbols.GOAL_MARK);
    }

    public boolean containQueryVar() {
        return (content.getName().indexOf(Symbols.VAR_QUERY) >= 0);
    }

    public boolean getRevisible() {
        return revisible;
    }

    public void setRevisible(boolean b) {
        revisible = b;
    }

    /**
     * Get a String representation of the sentence
     *
     * @return The String
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(content.toString());
        s.append(punctuation).append(" ");
        if (truth != null) {
            s.append(truth.toString());
        }
        s.append(stamp.toString());
        return s.toString();
    }

    /**
     * Get a String representation of the sentence, with 2-digit accuracy
     *
     * @return The String
     */
    public String toStringBrief() {
        return toKey() + stamp.toString();
    }
    
    public String toStringBrief(String tense){
        return toKey(tense) + stamp.toString();
    }

    /**
     * Get a String representation of the sentence for key of Task and TaskLink
     *
     * @return The String
     */
    public String toKey() {
        StringBuilder s = new StringBuilder();
        s.append(content.toString());
        s.append(punctuation).append(" ");       
        
        if (truth != null) {
            s.append(truth.toStringBrief());
        }

        return s.toString();
    }
    
    public String toKey(String tense){
        
        StringBuilder s = new StringBuilder();
        s.append(content.toString());
        s.append(punctuation).append(" ");       
        s.append(tense).append(" ");
        if (truth != null) {
            s.append(truth.toStringBrief());
        }

        return s.toString();
        
    }
    
    public boolean isEternal(){
        
        return stamp.isEternal();
        
    }
    
    /**
     * 将一个语句投影到另一个目标时间,如果所要投影的时间为永恒
     * 那么语句将从事件变为永恒
     * @param targetTime
     * @param currentTime
     * @param memory
     * @return 
     */
    public Sentence projection(long targetTime, long currentTime, Memory memory){
        
        TruthValue newTruth = projectionTruth(targetTime, currentTime, memory);
        boolean eternalizing = newTruth.isEternal();
        
        Stamp newStamp = new Stamp(stamp);
        
        if(eternalizing)
            newStamp.setOccurrenceTime(Stamp.ETERNAL);
        else
            newStamp.setOccurrenceTime(targetTime);
        
        return new Sentence(content, punctuation, newTruth, newStamp, false);
        
    }
    
    /**
     * 投影真值，将一个真值按照给定的目标事件进行投影
     * @param targetTime
     * @param currentTime
     * @param memory
     * @return 
     */
    public TruthValue projectionTruth(long targetTime, long currentTime, Memory memory){
        
        TruthValue newTruth = null;
        // 如果当前时间戳不是永恒的
        if(!stamp.isEternal()){
            // 先永恒化当前的真值
            newTruth = TruthFunctions.eternalize(truth);
            
                if(targetTime != Stamp.ETERNAL){
                    long occurrenceTime = stamp.getOccurrenceTime();
                    // 按照目标时间算出更新信念的参数
                    float factor = TruthFunctions.temporalProjection(occurrenceTime, targetTime, currentTime);
                    // 更新信心
                    float newConfidence = factor * truth.getConfidence();
                    // 如果新的信心大于永恒化之后的信心，创建一个以新的信心为信心的真值                   
                    if(newConfidence > newTruth.getConfidence())
                        newTruth = new TruthValue(truth.getFrequency(), newConfidence, truth.getAnalytic(), false);
                }           
        }
        
        // 如果newTruth为空，证明当前语句为永恒，则直接返回当前真值，永恒不能非永恒化
        if(newTruth == null)
            newTruth = truth.clone();
        
        return newTruth;
        
    }
    
}
