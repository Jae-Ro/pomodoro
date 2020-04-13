import React, { Component } from 'react';
import UserDashboard from './UserDashboard';
import AdminDashboard from './AdminDashboard';
import UserReport from './UserReport';
import AdminReport from './AdminReport';
import Header from './Header.js';
import Footer from './Footer.js';
import { Route, Switch } from 'react-router-dom';

class ProtectedRoutes extends Component {
    render() {
        return (
            <div>
                <Header />
                <div className='ptt'>
                    <Switch>
                        <Route exact path='/ptt/admin' component={AdminDashboard} />
                        <Route exact path='/ptt/admin/report' component={AdminReport} />
                        <Route exact path="/ptt/users/:id" component={UserDashboard} />
                        <Route exact path="/ptt/users/:id/report" component={UserReport} />
                    </Switch>
                </div>
                <Footer />
            </div>
        );
    }
}
export default ProtectedRoutes;
