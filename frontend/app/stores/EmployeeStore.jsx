var DBService = require('../DBService'),
    Reflux = require('reflux'),
    LoginStore = require('./LoginStore');

module.exports = Reflux.createStore({
    employees: [],

    getEmployees: function(){
        return this.employees;
    },
    setEmployees: function(employees){
        this.employees = _.sortBy(employees, function(employee){ // sort activities by name
            return employee.name;
        });

        this.trigger(this.employees);
    },
    fetchEmployees: function () {
        DBService.get('/employees.json?&max=-1', this.setEmployees);
    },

    init: function(){
        LoginStore.listen(this.fetchEmployees);
    }
});