/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nars.storage;

import java.util.ArrayList;
import nars.entity.Task;
import nars.main_nogui.Parameters;

/**
 *
 * @author Xiang
 */
public class Buffer extends Bag<Task>{
    
    private int duration;
    private Memory memory;
    private String name;
    
    public Buffer(Memory memory) {
        super(memory);
        this.memory = memory;
        this.duration = 0;
    }
    
    public Buffer(Memory memory, int duration, String name)
    {
        super(memory);
        this.memory = memory;
        this.duration = duration;
        this.name = name;
    }
    
    public int getDuration(){
        return duration;
    }
    
    public void setDuration(int duration){
        this.duration = duration;
    }
    
    public String getName(){
        return name;
    }

    @Override
    public boolean putIn(Task task){
        task.getSentence().getStamp().setPutInTime(memory.getTime());
        return super.putIn(task);
    } 
    
    public void clearExpiredItem(){
       
        ArrayList<String> expiredKey = new ArrayList<String>();
        
        (this.getNameTable()).forEach((key, task) -> {
            if((memory.getTime() - task.getSentence().getStamp().getPutInTime()) > duration){
                expiredKey.add(key);
            }
        });
        
        for (int i = 0; i < expiredKey.size(); i++) {
            Task task = this.getNameTable().get(expiredKey.get(i));
            super.pickOut(task.getKey());
        }
    
    }
    
    @Override
    public Task takeOut(){
        clearExpiredItem();
        return super.takeOut();
    }
    
    @Override
    protected int capacity() {
        return Parameters.TASK_BUFFER_SIZE;
    }

    @Override
    protected int forgetRate() {
        return Parameters.NEW_TASK_FORGETTING_CYCLE;
    }
    
}
