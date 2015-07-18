package net.ashald.envfile;

import com.jetbrains.python.run.PyCommonOptionsFormData;
import com.jetbrains.python.run.PyCommonOptionsFormFactory;


public class PyEnvFileCommonOptionsFormFactory extends PyCommonOptionsFormFactory {
    @Override
    public PyEnvFileIdeCommonOptionsForm createForm(PyCommonOptionsFormData data){
        return new PyEnvFileIdeCommonOptionsForm(data);
    }
}