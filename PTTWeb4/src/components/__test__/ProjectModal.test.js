import React from 'react';
import ReactDOM from 'react-dom';
import ProjectModal from '../ProjectModal';
import renderer from 'react-test-renderer';
import {shallow} from 'enzyme';
import toJSON from 'enzyme-to-json';
import { Provider } from "react-redux";
import configureMockStore from "redux-mock-store";

const mockStore = configureMockStore();
const store = mockStore({});


test("ProjectModal matches its snapshot",()=>{
    const wrapper = shallow(<Provider store={store}> <ProjectModal /> </Provider>)

    expect(toJSON(wrapper)).toMatchSnapshot();
})

import {actionCreators} from '../../models/actions/projectModalActions';
// import * as types from '../../constants/ActionTypes'
import { actionTypes } from '../../models/actions/projectModalActions';


describe('ProjectModal actions', () => {
  it('should open a project modal', () => {
    const expectedAction = {
        type: actionTypes.OPEN_PROJECT_MODAL
    }
    expect(actionCreators.openProjectModal()).toEqual(expectedAction)
  })
  it('should close a project', () => {
    const expectedAction = {
        type: actionTypes.CLOSE_PROJECT_MODAL
    }
    expect(actionCreators.closeProjectModal()).toEqual(expectedAction)
  })

})