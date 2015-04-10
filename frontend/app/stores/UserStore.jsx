var DBService = require('../DBService'),
    Reflux = require('reflux'),
    LoginStore = require('./LoginStore');

module.exports = Reflux.createStore({
    users: [],

    getUsers: function(){
        return this.users;
    },
    setUsers: function(users){
        this.users = _.sortBy(users, function(user){ // sort activities by name
            return user.name;
        });

        this.trigger(this.users);
    },
    fetchUsers: function () {
        DBService.get('/users.json?&max=-1', this.setUsers);
    },

    init: function(){
        LoginStore.listen(this.fetchUsers);
    }
});